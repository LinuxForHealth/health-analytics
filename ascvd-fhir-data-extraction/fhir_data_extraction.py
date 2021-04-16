import json
from datetime import date
from dateutil.parser import parse
from flask import Flask, jsonify, request
from flask_restful import Resource, Api, reqparse, abort, marshal, fields
from pip._vendor.pyparsing import line

app = Flask(__name__)

def extract_age(resourceArray):
    birthDate = None
    for resource in resourceArray:
        if resource['resourceType']=='Patient':
            if 'birthDate' in resource:
                birthDate = resource['birthDate']
    if birthDate != None:
        parts = birthDate.split('-')
        born = date(int(parts[0]), int(parts[1]), int(parts[2]))
        today = date.today()
        return today.year - born.year - ((today.month, today.day) < (born.month, born.day))
    return None

def extract_resourceType(resourceArray, resourceType):
    resources=[]
    for resource in resourceArray:
        if resource['resourceType'] == resourceType:
            resources.append(resource)
    return resources

def extract_conditions(resourceArray):
    conditions=[]
    for resource in resourceArray:
        if resource['resourceType'] == 'Condition':
            if 'code' in resource:
                code = resource['code']
                if 'coding' in code:
                    for coding in code['coding']:
                        if 'display' in coding:
                            conditions.append(coding['display'])
    
    return conditions

def extract_gender(resourceArray):
    for patient in extract_resourceType(resourceArray, 'Patient'):
        if 'gender' in patient:
            return patient['gender']
    return None

def extract_race(resourceArray):
    # Is the patient AfricanAmerican?
    # FIXME Not sure how/where to get race from...
    return None

def extract_hdl(sorted_observations):
    for observation in sorted_observations:
        if 'code' in observation:
            code = observation['code']
            if 'coding' in code:
                for coding in code['coding']:
                    if 'display' in coding:
                        if coding['display']=='High Density Lipoprotein Cholesterol':
                            return observation['valueQuantity']['value']
    return None

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
                    return component['valueQuantity']['value']
    return None

def extract_cholesterol(sorted_observations):
    for observation in sorted_observations:
        if 'code' in observation:
            code = observation['code']
            if 'coding' in code:
                for coding in code['coding']:
                    if 'display' in coding:
                        if coding['display'] == 'Total Cholesterol':
                            return observation['valueQuantity']['value']
    return None

def extract_diabetic(sorted_observations):
    conditions = extract_conditions(sorted_observations)
    for condition in conditions:
        if condition == 'Diabetes':
            return True
    return False



def extract_is_smoker(sorted_observations):
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
                                return True
    return False


def extract_bp_treated(sorted_observations):
    return False # Assuming False for now


@app.route("/", methods=['POST'])
def extract_ascvd_input_rest():
    resourceArray = []
    request_data = request.data.decode("utf-8")
    try:
        # if a single fhir bundle is passed in, add it to the resourceArray
        jsonObject = json.loads(request_data)
        if 'resourceType' in jsonObject and jsonObject['resourceType'] == 'Bundle' and 'entry' in jsonObject:
            resourceArray.append(jsonObject)
    except ValueError:
        pass

    if not resourceArray:
        for line in request_data.splitlines():
            # if multiple lines are passed in, process each line separately
            json_line = json.loads(line)
            if 'resourceType' in json_line and json_line['resourceType'] == 'Bundle' and 'entry' in json_line:
                # this looks like a formatted bundle
                entries = json_line["entry"]
                for entry in entries:
                    resourceArray.append(entry["resource"])
            else:
                resourceArray.append(json_line)
    return extract_ascvd_input(resourceArray)

def extract_ascvd_input(resourceArray):
    data = {}
    
    # Age
    data['age'] = extract_age(resourceArray)

    # isMale
    data['gender'] = extract_gender(resourceArray)

    # isAfricanAmerican
    data['race'] = extract_race(resourceArray)

    observations = extract_resourceType(resourceArray, 'Observation')
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