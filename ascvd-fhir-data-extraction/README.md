# ASCVD FHIR Data Extraction

This repository contains a simple API that will extract important data from a set of FHIR resources required to calculate the ten year risk of cardiovascular disease based on the ASCVD project.

## Build

### Pre-Requisites

- Python 3.8+

### Building

Check out the source and build the docker image:

1. `git clone https://github.com/Alvearie/health-analytics.git`
1. `cd health-analytics/ascvd-fhir-data-extraction`
1. `docker build . -t <<GROUP>>/ascvd_fhir_data_extraction:1.0-SNAPSHOT`
1. `docker push <<GROUP>>/ascvd_fhir_data_extraction:1.0-SNAPSHOT`


### Deploying

This docker image can be run in standalone docker via:

`docker run -p:5000:5000 <<GROUP>>/ascvd_fhir_data_extraction:1.0-SNAPSHOT`

It can also be run in kubernetes using kubernetes.yaml.  However, you will first need to update two values in that file:

<<INGRESS_SUBDOMAIN>> - Update this to point to your kubernetes ingress subdomain.  If you prefer, you can replace the entire "host" value for the ingress.
<<GROUP>> - Update this to match the group value used above when building docker.

After updating these two parameters, you can deploy the ASCVD FHIR Data Extraction API using 

`kubectl apply -f kubernetes.yml`


### Running

In order to call this API, you simply need to POST a set of FHIR resources to the exposed endpoint.  The resources should not be in a FHIR structure (bundle/etc.), just one resource per line.