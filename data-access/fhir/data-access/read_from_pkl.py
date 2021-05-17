'''
Created on May 10, 2021

@author: atclark
'''

import numpy as np
import requests
import time
import pandas

from keras.utils.data_utils import get_file
from tensorflow.python.util.tf_export import keras_export

def export_fhir_from_cos(
        fhir_url = 'https://atc-ingestion-fhir.integration-k8s-cluster-dcc48c44d831198cb8496b1ec68d7d12-0000.us-south.containers.appdomain.cloud/fhir-server/api/v4',
        fhir_user = 'fhiruser',
        fhir_pwd = 'integrati0n',
        resource_types = ['Patient','Observation','Condition'],
        cleanup = True,
        store_locally = True):
  """Loads data from a FHIR server into analysis-ready format.
  """

  data = []
  for resource_type in resource_types:
    cos_url = export(fhir_url, fhir_user, fhir_pwd, resource_type)
    if (cos_url != None):
        # retrieve data
        cos_data = pandas.read_json(cos_url, lines = True)
        
        if (store_locally):
             # persist
             picklefile = "df." + cos_url[cos_url.rindex("/")+1:] + ".pkl"
             cod_data.to_pickle(root_dir + "/" + picklefile)
  
        if cleanup:
            delete(cos_url)
            data[resource_type] = cos_data
        else:
            data[resource_type] = (cos_url, cos_data)
   
  return data

def export(fhir_url, fhir_user, fhir_pwd, resource_type):
    export_response = requests.get(
        url = fhir_url + "/$export", 
        auth = (fhir_user, fhir_pwd), 
        params = {'_outputFormat': 'application/fhir+ndjson', '_type': resource_type },
        verify = False)
    
    if export_response.status_code != 202:
        print("ERROR in export")
    else:
        # export in progress since response code is 202
        # get and check status-need url
        content_location = export_response.headers["Content-Location"]  # need to call this until 200 (or error)
        statusresp = requests.get(
            content_location, 
            auth = (fhir_user, fhir_pwd), 
            verify = False)

        respStatusCode = statusresp.status_code
        while respStatusCode == 202:
            time.sleep(2)  # wait for a bit to see if export completes
            statusresp = requests.get(
                content_location, 
                auth = (fhir_user, fhir_pwd), 
                verify = False)
            respStatusCode = statusresp.status_code

        if respStatusCode == 200:
            # export is done, get the object name from status response, else other code means error
            return statusresp.json()["output"][0]['url']
    return None
    

if __name__ == '__main__':
  load_data()