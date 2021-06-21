import json
import os
import requests
from datetime import date
from dateutil.parser import parse
from flask import Flask, request

# docker build . -t atclark/ascvd_from_fhir:0.0.1
# docker push atclark/ascvd_from_fhir:0.0.1
# kubectl apply -f kubernetes.yml

app = Flask(__name__)

def extract_age(resource_array):
    age = {}
    for resource in resource_array:
        if resource['resourceType']=='Patient':
            if 'birthDate' in resource:
                age['resource_id'] = resource['id']
                birthDate = resource['birthDate']
                if birthDate != None:
                    parts = birthDate.split('-')
                    born = date(int(parts[0]), int(parts[1]), int(parts[2]))
                    today = date.today()
                    age['value'] = today.year - born.year - ((today.month, today.day) < (born.month, born.day))
    return age

def extract_resourceType(resource_array, resourceType):
    resources = []
    for resource in resource_array:
        if resource['resourceType'] == resourceType:
            resources.append(resource)
    return resources

def extract_conditions(resource_array):
    conditions = []
    for resource in resource_array:
        if resource['resourceType'] == 'Condition':
            if 'code' in resource:
                code = resource['code']
                if 'coding' in code:
                    for coding in code['coding']:
                        if 'display' in coding:
                            conditions.append(coding['display'])
    
    return conditions

def extract_gender(resource_array):
    gender = {}
    for patient in extract_resourceType(resource_array, 'Patient'):
        if 'gender' in patient:
            gender['resource_id'] = patient['id']
            gender['value'] = patient['gender']
    return gender

def extract_race(resource_array):
    # Is the patient AfricanAmerican?
    # FIXME Not sure how/where to get race from...
    return {}

def extract_hdl(sorted_observations):
    for observation in sorted_observations:
        if 'code' in observation:
            code = observation['code']
            if 'coding' in code:
                for coding in code['coding']:
                    if 'display' in coding:
                        if coding['display']=='High Density Lipoprotein Cholesterol':
                            hdl = {}
                            hdl['resource_id'] = observation['id']
                            hdl['patient_id'] = observation['subject']['reference']
                            hdl['value'] = observation['valueQuantity']['value']
                            return hdl
    return {}

def extract_systolic(sorted_observations):
    for observation in sorted_observations:
        isBP = False
        if 'code' in observation:
            code = observation['code']
            if 'coding' in code:
                for coding in code['coding']:
                    if 'display' in coding:
                        if coding['display'] == 'Blood Pressure':
                            isBP = True
        if isBP:
            for component in observation['component']:
                if component['code']['text'] == 'Systolic Blood Pressure':
                    systolic = {}
                    systolic['resource_id'] = observation['id']
                    systolic['patient_id'] = observation['subject']['reference']
                    systolic['value'] = component['valueQuantity']['value']
                    return systolic
    return {}

def extract_cholesterol(sorted_observations):
    for observation in sorted_observations:
        if 'code' in observation:
            code = observation['code']
            if 'coding' in code:
                for coding in code['coding']:
                    if 'display' in coding:
                        if coding['display'] == 'Total Cholesterol':
                            total_cholesterol = {}
                            total_cholesterol['resource_id'] = observation['id']
                            total_cholesterol['patient_id'] = observation['subject']['reference']
                            total_cholesterol['value'] = observation['valueQuantity']['value']
                            return total_cholesterol
    return {}

def extract_diabetic(sorted_observations):
    diabetic = {}
    diabetic['value'] = False
    conditions = extract_conditions(sorted_observations)
    for condition in conditions:
        if condition == 'Diabetes':
            diabetic['resource_id'] = condition['id']
            diabetic['patient_id'] = condition['subject']['reference']
            diabetic['value'] = True
    return diabetic

def extract_is_smoker(sorted_observations):
    is_smoker = {}
    is_smoker['value'] = False
        #Is the patient a current smoker
        # 449868002Current every day smoker  ***
        # 428041000124106Current some day smoker   ***
        # 8517006Former smoker
        # 266919005Never smoker
        # 77176002Smoker, current status unknown
        # 266927001Unknown if ever smoked
        # 428071000124103Current Heavy tobacco smoker   ***
        # 428061000124105Current Light tobacco smoker   ***
    SMOKERS = ["Current every day smoker",
               "Current some day smoker",
               "Current Heavy tobacco smoker",
               "Current Light tobacco smoker"]
    for observation in sorted_observations:
        if 'code' in observation:
            code = observation['code']
            if 'coding' in code:
                for coding in code['coding']:
                    if 'display' in coding:
                        if coding['display']=='Tobacco smoking status NHIS':
                            text = observation['valueCodeableConcept']['text']
                            if text in SMOKERS:
                                is_smoker['resource_id'] = observation['id']
                                is_smoker['patient_id'] = observation['subject']['reference']
                                is_smoker['value'] = True
    return is_smoker

def extract_bp_treated(sorted_observations):
    bp_treated = {}
    bp_treated['value'] = False
    return bp_treated # Assuming False for now


    
def get_resources_from_request(request_data):
    resource_array = []
    try:
        # if a single fhir bundle is passed in, add it to the resource_array
        json_object = json.loads(request_data)
        if 'resourceType' in json_object and json_object['resourceType'] == 'Bundle' and 'entry' in json_object:
            entries = json_object["entry"]
            for entry in entries:
                resource_array.append(entry["resource"])
    except ValueError:
        pass

    if not resource_array:
        for line in request_data.splitlines():
            # if multiple lines are passed in, process each line separately
            json_line = json.loads(line)
            if 'resourceType' in json_line and json_line['resourceType'] == 'Bundle' and 'entry' in json_line:
                # this looks like a formatted bundle
                entries = json_line["entry"]
                for entry in entries:
                    resource_array.append(entry["resource"])
            else:
                resource_array.append(json_line)
    return resource_array

def extract_ascvd_input_data(resource_array):
    data = {}
    
    # Age
    data['age'] = extract_age(resource_array)

    # isMale
    data['gender'] = extract_gender(resource_array)

    # isAfricanAmerican
    data['race'] = extract_race(resource_array)

    observations = extract_resourceType(resource_array, 'Observation')
    sorted_observations = sorted(observations, key=lambda o: parse(o['effectiveDateTime']), reverse = True)

    # total_cholesterol
    data['total_cholesterol'] = extract_cholesterol(sorted_observations)

    # hdl
    data['hdl'] = extract_hdl(sorted_observations)

    # systolic
    data['systolic_bp'] = extract_systolic(sorted_observations)

    # isDiabetic
    data['diabetic'] = extract_diabetic(sorted_observations)

    # isSmoker
    data['is_smoker'] = extract_is_smoker(sorted_observations)
        
    # bpTreated
    data['bp_treated'] = extract_bp_treated(sorted_observations)
    
    return data

def calculate_ascvd(ascvd_data):
    ascvd_url = os.getenv('ASCVD_URL')
    ascvd_params = {}
    if ('value' in ascvd_data['age']):
        ascvd_params['age'] = ascvd_data['age']['value']

    if ('value' in ascvd_data['gender']):
        ascvd_params['male'] = ascvd_data['gender']['value']=='male'
    
    if ('value' in ascvd_data['race']):
        ascvd_params['africanAmerican'] = ascvd_data['race']['value']=='africanAmerican'
    
    if ('value' in ascvd_data['bp_treated']):
        ascvd_params['bpTreated'] = ascvd_data['bp_treated']['value']
    
    if ('value' in ascvd_data['is_smoker']):
        ascvd_params['currentSmoker'] = ascvd_data['is_smoker']['value']
    
    if ('value' in ascvd_data['diabetic']):
        ascvd_params['diabetic'] = ascvd_data['diabetic']['value']
    
    if ('value' in ascvd_data['total_cholesterol']):
        ascvd_params['totalCholesterol'] = ascvd_data['total_cholesterol']['value']
    
    if ('value' in ascvd_data['hdl']):
        ascvd_params['hdlCholesterol'] = ascvd_data['hdl']['value']
    
    if ('value' in ascvd_data['systolic_bp']):
        ascvd_params['systolicBp'] = ascvd_data['systolic_bp']['value']

    # Call ASCVD 
    resp = requests.get(ascvd_url, params=ascvd_params, verify=False)
    ascvd_output=resp.json()
    if 'tenYearRisk' in ascvd_output:
      return ascvd_output['tenYearRisk']

    return None

def build_ascvd_risk_assessment(ascvd_data, ten_year_risk):
    patient_id = None
    encounter_date_time=date.today().strftime("%Y-%m-%dT%H:%M:%SZ")

    referenced_resources = set()
    for item in ascvd_data.values():
        if 'patient_id' in item:
            patient_id = item['patient_id']
            if (patient_id.startswith("Patient/")):
                referenced_resources.add(patient_id[9:])
            else:
                referenced_resources.add(patient_id)
        
        if 'resource_id' in item:
            referenced_resources.add(item['resource_id'])
            
    fhirResp = {}
    fhirResp['resourceType'] = 'RiskAssessment'
    fhirResp['id'] = 'cardiac'
    # fhirResp['text'] = {}
    # fhirResp['text']['status'] = 'additional'
    # fhirResp['text']['div'] = ''
    # fhirResp['identifier'] = []
    # identifier = {}
    # identifier['use'] = 'official'
    # identifier['system'] = 'http://example.org'
    # identifier['value'] = 'risk-assessment-cardiac'
    # fhirResp['identifier'].append(identifier)
    fhirResp['status'] = 'final'
    if (patient_id != None):
        fhirResp['subject'] = {}
        fhirResp['subject']['reference'] = patient_id
    # fhirResp['encounter'] = {}
    # fhirResp['encounter']['reference'] = 'Encounter/example'
    fhirResp['occurrenceDateTime'] = encounter_date_time
    # fhirResp['performer'] = {}
    # fhirResp['performer']['display'] = 'http://cvdrisk.nhlbi.nih.gov/#cholesterol'
    fhirResp['basis'] = []
    for referenced_resource in referenced_resources:
        ref={}
        if referenced_resource.startswith("urn:uuid"):
            ref['reference'] = referenced_resource
        else:
            ref['reference'] = "urn:uuid:"+referenced_resource
        
        fhirResp['basis'].append(ref)
    
    fhirResp['prediction'] = []
    prediction = {}
    outcome = {}
    outcome['text'] = 'atherosclerotic cardiovascular disease'
    prediction['outcome'] = outcome
    prediction['probabilityDecimal'] = ten_year_risk
    # whenRange = {}
    # low = {}
    # low['value'] = 39
    # low['unit'] = 'years'
    # low['system'] = 'http://unitsofmeasure.org'
    # low['code'] = 'a'
    # whenRange['low'] = low
    # high = {}
    # high['value'] = 49
    # high['unit'] = 'years'
    # high['system'] = 'http://unitsofmeasure.org'
    # high['code'] = 'a'
    # whenRange['high'] = high
    # prediction['whenRange'] = whenRange
    fhirResp['prediction'].append(prediction)
    return fhirResp



@app.route("/extract", methods=['POST'])
def extract_ascvd_input_from_fhir():
    request_data = request.data.decode("utf-8")
    resource_array=get_resources_from_request(request_data)
    return extract_ascvd_input_data(resource_array)


@app.route("/fhir", methods=['POST'])
def update_bundle_with_ascvd_risk_assessment():
    ascvd_data = extract_ascvd_input_from_fhir()
    ten_year_risk = calculate_ascvd(ascvd_data)

    # Insert ASCVD RiskAssessment into original bundle and return
    request_data = request.data.decode("utf-8")
    request_json = json.loads(request_data)

    if ten_year_risk != None:
      # Build RiskAssessment FHIR resource
      riskAssessment = build_ascvd_risk_assessment(ascvd_data, ten_year_risk)
      resource = {}
      resource['resource'] = riskAssessment
      request_json['entry'].append(resource)

    return request_json