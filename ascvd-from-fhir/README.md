# ASCVD From FHIR

This repository contains API's that will help process FHIR resources to extract the necessary data to calculate the ten year risk of cardiovascular disease, based on the ASCVD project.  The two defined API's are:

`extract` - This will extract the key input data required by ASCVD

`fhir` - This will extract the necessary data, call ASCVD, and add a corresponding RiskAssessment FHIR resource to the provided bundle, representing the ASCVD result

## Build

### Pre-Requisites

- Python 3.8+

### Building

Check out the source and build the docker image:

1. `git clone https://github.com/LinuxForHealth/health-analytics.git`
1. `cd health-analytics/ascvd-from-fhir`
1. `docker build . -t <<GROUP>>/ascvd-from-fhir:1.0-SNAPSHOT`
1. `docker push <<GROUP>>/ascvd-from-fhir:1.0-SNAPSHOT`


### Deploying

This docker image can be run in standalone docker via:

`docker run -p:5000:5000 <<GROUP>>/ascvd_from_fhir:1.0-SNAPSHOT --env ASCVD_URL=<<ASCVD_SERVICE_URL>>`

It can also be run in kubernetes using kubernetes.yaml.  However, you will first need to update four values in that file:

`<<INGRESS_CLASS>>` - Update this to match the ingress class for your target cloud environment.

`<<INGRESS_SUBDOMAIN>>` - Update this to point to your kubernetes ingress subdomain.  If you prefer, you can replace the entire "host" value for the ingress.

`<<GROUP>>` - Update this to match the group value used above when building docker.

`<<ASCVD_SERVICE_URL>>` - Update this to match the URL of the previously deployed [ASCVD service](../ascvd)

After updating these parameters, you can deploy the ASCVD FHIR Data Extraction API using:

`kubectl apply -f kubernetes.yml`


### Running

In order to call this API, you simply need to POST FHIR resources to one of the exposed endpoints:

`<<URL>>/extract` - This will extract the values from FHIR required to call ASCVD. The input should be one of the following:
1. A FHIR bundle
1. A set of FHIR resources, one per line
1. A set of FHIR bundles, one per line


`<<URL>>/fhir` - This will add a FHIR resource (RiskAssessment) to the provided FHIR bundle representing the ASCVD result based on the provided data.  The input should be a FHIR bundle.
