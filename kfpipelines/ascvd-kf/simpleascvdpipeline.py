import kfp
import kfp.components as comp
import kfp.dsl as dsl

# This kubeflow pipeline will extract data for a patient from a fhir server and
# send it to a microservice to calculate the ascvd cardiac risk assessment.

def get_data_and_calculate_ascvd(patientid: str = '',
                   fhirEndpoint: str = '',
                   username: str = '',
                   password: str = '',
                   ascvdEndpoint: str = ''
                   ):

    import requests
    import json

    #Load the data from fhir with $everything
    #Call extract which will in turn call calculation
    resp = requests.get(fhirEndpoint + "/Patient/" + patientid + "/$everything" + "?_format=json", auth=(username, password), verify=False)
    print("Patient $everything: ", resp.status_code)

    json_formatted_str = ""
    if resp.status_code == 200:
        calculate_resp = requests.post(url=ascvdEndpoint, json=resp.json())
        print("ASCVD: ", calculate_resp.status_code)
        json_formatted_str = json.dumps(calculate_resp.json(), indent=2)
        print(json_formatted_str)
    else:
        print("ERROR--$everything status ended with non 200 code-no object found")



#This is the actual pipeline definition
@dsl.pipeline(
   name='Fhir Single Patient ASCVD Pipeline',
   description='Load patient from fhir and run model'
)
def single_patient_ascvd_pipeline( #pipeline needs these items to do its work
    fhirEndpoint = '',
    username = '',
    password = '',
    patientid = '',
    ascvdEndpoint = ''
):

    #Set up a pipeline step
    processOp = comp.func_to_container_op(get_data_and_calculate_ascvd, base_image='python:3.8', packages_to_install=['requests'])

    process_task = processOp(patientid, fhirEndpoint, username, password, ascvdEndpoint)


if __name__ == '__main__':
    from kfp_tekton.compiler import TektonCompiler
    TektonCompiler().compile(single_patient_ascvd_pipeline, 'simpleascvdpipeline.yaml')
