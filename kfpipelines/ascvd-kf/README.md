# ASCVD and Kubeflow Pipelines

This folder contains an example of a kubeflow pipeline that calculates an analytic result
for a patient whose data is pulled from a Fhir server. The analytic is known as ASCVD (a measure of cardiovascular risk).  The kubeflow pipeline will take a Fhir patient identifier, get all the data for that patient, and then call off to a microservice that can extract the proper data and calculate the risk value.

## Prerequisites
- an accessible kubeflow deployment
- an accessible fhir server
- an accessible ASCVD microservice deployment that includes both
data extraction and ascvd calculation https://github.com/Alvearie/health-analytics/tree/main/ascvd-from-fhir
- installed kubeflow pipelines sdk (`pip install kfp-tekton`)
- clone of the Health Analytics repository (cd to the ascvd-kf directory)
    https://github.com/Alvearie/health-analytics

For Variation 2 described below you will also need:
- Docker installed on your desktop
- a dockerhub account
- access to a kubernetes cluster via kubectl



## Setup and use

There are two different example variations for setting up and using this pipeline showing both a manual kubeflow interaction as well as a microservice based integration.

### Variation 1: Standard Kubeflow pipelines (compile, upload, and run)

This variation uses a typical Python kubeflow pipeline definition that is compiled and manually uploaded before starting the pipeline. After cloning the repository, navigate to the correct directory and compile the pipeline.

1. `git clone https://github.com/Alvearie/health-analytics.git`
1. `cd kfpipelines/ascvd-kf` directory
1. `python simpleascvdpipeline.py`

This will create `simpleascvdpipeline.yaml` which is a yaml definition for the pipeline.

In the kubeflow dashboard, choose Pipelines and +Upload pipeline.  Pick the `simpleascvdpipeline.yaml` file from the previous step.  Then, choose Experiments and New experiment.  Fill in a name and description and pick next.  You can configure a run by picking a pipeline (the one you just uploaded) and providing a run name.

Finally, you will need to provide the Run Parameters for this pipeline before selecting `Start`.  The required run parameters are:

#### fhirEndpoint
    Full url for the fhir server containing the patient data
    Example: https://dlr-ingestion-fhir.wh-health-patterns.dev.watson-health.ibm.com/fhir-server/api/v4

#### username
    A fhir server user
    Example: fhiruser

#### password
    The configured password for the username fhir user
    Example: mypassw0rd

#### patientid
    The id of the patient as it appears in fhir
    Example: 17a390cc3f6-8d67b65d-b558-4fd4-a2c0-4d41624e0773

#### ascvdEndpoint
    The full url for the ascvd microservice
    Example: https://dlr-ascvd-from-fhir.wh-health-patterns.dev.watson-health.ibm.com/fhir


### Variation 2: Kubeflow pipelines with a microservice wrapper

This variation will utilize a wrapper microservice to directly run a kubeflow pipeline from its function definition.  No manual compiling or uploading of the pipeline is needed.



##### ascvd-kf.py

`ascvd-kf.py` is a deployable flask-based (`https://flask.palletsprojects.com`) service that will start a kubeflow pipeline without the need to upload first.  The service defines 3 api endpoints described below.

##### api endpoints

- general health of the service-a simple sanity check on the service

    (GET)  https://\<\<ascvd-kf base url\>\>/healthcheck

- start the pipeline

    (POST)  https://\<\<ascvd-kf base url\>\>

    post data will be of type json containing the id of the patient of interest, an authorization cookie for kubeflow access, and an experiment name.  The kubeflow run will be placed in the configured experiment (`default` if omitted).  

    ```
{
    "patientid": "17a390cc3f6-8d67b65d-b558-4fd4-a2c0-4d41624e0773",
    "sessioncookie": "authservice_session=MTYyNDQ1MTgxNHxOd3dBTkZsSFF6UkZUVkpOUTBoV1IxbFdTalpXTkZsRk1sZFZNMVJWU1RjMVZGRk9WRlpUV0VWRU4xZFVObGhKUkZCSFFsUk5WMEU9fM9wEF7nSHB8Te4RjAAeFbF3auKEbnAySXNWQvC3nMu7",
    "experimentname": "Demo0623C"
}
    ```

    The easiest way to get an authservice_session cookie is to use the browser developer tools (for example with firefox you can look at Tools->Browser Tools->Web Developer Tools and pick Storage to see cookie values)

- status of the run

    (GET)  https://\<\<ascvd-kf base url\>\>/statuscode

    returns information about the run

    post data will be json containing the id of the run (the run id is returned by the start operation) and an authorization cookie for kubeflow access.

    ```
  {
    "runid": "95912c64-7e46-4133-8fa7-21aa772ffe48",
    "sessioncookie": "authservice_session=MTYyNDQ1MTgxNHxOd3dBTkZsSFF6UkZUVkpOUTBoV1IxbFdTalpXTkZsRk1sZFZNMVJWU1RjMVZGRk9WRlpUV0VWRU4xZFVObGhKUkZCSFFsUk5WMEU9fM9wEF7nSHB8Te4RjAAeFbF3auKEbnAySXNWQvC3nMu7"
  }
    ```

##### Deployment of the ascvd-kf microservice
In order to deploy the ascvd-kf service, you will need to build a docker image and push it out to a docker repository.  Then, in kubernetes you can deploy the service.  The included `Dockerfile` and kubernetes deployment file `kubernetes.yml` will assist with this process.

###### Dockerfile
Defines the process used to build the docker image

Build the docker image providing a `GROUP` and `TAG`

`docker build -t <<GROUP>>/ascvd-kf:<<TAG>> .`
    Example: `docker build -t dlranum/ascvd-kf:0.1.0`

Push the docker image to the repository

`docker push <<GROUP>>/ascvd-kf:<<TAG>>`
    Example: `docker push dlranum/ascvd-kf:0.1.0`

Once your image is pushed to the repository, you can substitute it in the kubernetes deployment yaml file (line 50).

`          image: <<GROUP>>/ascvd-kf:<<TAG>>`

In addition to the image identifier, there are a number of other substitutions that need to be made in the yaml file.

- Ingress class-pick from the provided values (line 21)

    `kubernetes.io/ingress.class: <<ingress class>>`

- Ingress host is the defined ingress root for this service (line 27)

    `host: <<ingress host url>>`

- Environment variables (lines 52-64)

```
- name: FHIRENDPOINT
  value: "<<fhirserver base url>>/fhir-server/api/v4>>"
- name: FHIRUSERNAME
  value: "<<fhir server username>>"
- name: FHIRPW
  value: "<<fhir server password>>"
- name: KFENDPOINT
  value: "<<public kubeflow endpoint root url>>"
- name: KUBEFLOWPROFILENAME
  value: "<<kubeflow user profile name>>"
- name: ASCVDENDPOINT
  value: "<<ascvd service endpoint>>/fhir"
```

Once those changes are made, the service can be deployed using the `kubectl` apply method.

`kubectl apply -f kubernetes.yml`

Use `kubectl get pods` to check that the `ascvd-kf` pod is running or use the `healthcheck` endpoint to see that it can respond.

Once the service is deployed and running, posting data to the service root api with `Postman` or `Curl` will allow you to start the pipeline.
