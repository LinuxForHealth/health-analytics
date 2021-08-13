import json
import os
import requests
from datetime import date
from dateutil.parser import parse
from flask import Flask, request
from ascvd_data_extraction import *

# docker build . -t atclark/ascvd_from_fhir:0.0.1
# docker push atclark/ascvd_from_fhir:0.0.1
# kubectl apply -f kubernetes.yml

app = Flask(__name__)

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