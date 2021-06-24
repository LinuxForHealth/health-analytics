# a microservice for starting up an ascvd kubeflow pipeline

import os

from flask import Flask, request
from flask import jsonify

from datetime import datetime

from kfp_tekton import TektonClient

app = Flask(__name__)

# simple health check endpoint for sanity
@app.route("/healthcheck", methods=['GET'])
def healthcheck():

    now = datetime.now()
    current_time_date = now.strftime("%Y-%m-%d %H:%M:%S")
    return generate_response(200, {"message": "ASCVD-KUBEFLOW service is running..." + current_time_date + "GMT"})

# root endpoint with POST will start a kubeflow pipeline for ascvd
# expects posted content to include patient id, kubeflow cookie, and experiment name
from simpleascvdpipeline import single_patient_ascvd_pipeline

@app.route("/", methods=['POST'])
def startpipeline():
        post_data = request.json

        patientid = post_data["patientid"]
        session_cookie = post_data["sessioncookie"]
        expname = post_data["experimentname"]

        KUBEFLOW_PUBLIC_ENDPOINT_URL = os.getenv("KFENDPOINT")

        client = TektonClient(
            host=f'{KUBEFLOW_PUBLIC_ENDPOINT_URL}/pipeline',
            cookies=session_cookie
        )

        #This will run the pipeline in the default experiment
        arguments={}
        arguments = {
          'patientid': patientid,
          'fhirEndpoint': os.getenv("FHIRENDPOINT"),
          'username': os.getenv("FHIRUSERNAME"),
          'password': os.getenv("FHIRPW"),
          'ascvdEndpoint': os.getenv("ASCVDENDPOINT")
        }

        retvalue = client.create_run_from_pipeline_func(single_patient_ascvd_pipeline, arguments=arguments,
                                         experiment_name=expname, namespace=os.getenv("KUBEFLOWPROFILENAME"))

        #return the pipeline run id
        return generate_response(200, {"message": "Check pipeline",
                                       "retvalue": str(retvalue)})

# status endpoint with POST will return the status of a kubeflow run
# expects posted content to include run id and kubeflow cookie
@app.route("/status", methods=['POST'])
def querypipelinerun():
        post_data = request.json

        runid = post_data["runid"]
        session_cookie = post_data["sessioncookie"]

        KUBEFLOW_PUBLIC_ENDPOINT_URL = os.getenv("KFENDPOINT")

        client = TektonClient(
            host=f'{KUBEFLOW_PUBLIC_ENDPOINT_URL}/pipeline',
            cookies=session_cookie
        )

        retvalue = "status unknown"
        try:
            retvalue = client.get_run(runid)
        except Exception as e:
            print("an exception occurred", e)
        return generate_response(200, {"message": "Pipeline run details",
                                       "retvalue": str(retvalue)})

#helper function for responses
def generate_response(statuscode, otherdata={}):

    message = {
        "status": str(statuscode)
    }
    message.update(otherdata)
    resp = jsonify(message)
    resp.status_code = statuscode
    return resp



if __name__ == '__main__':
   app.run()
