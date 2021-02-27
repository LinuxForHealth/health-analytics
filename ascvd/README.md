# Million Hearts ASCVD Model

This project contains an implementation of the model used by [CMS](https://innovation.cms.gov/innovation-models/million-hearts-cvdrrm) to try and reduce patient cardiovascular risk with a monetary incentive for providers.  

## Technical details

This project can be run from a command line, as a docker container, as a kubernetes container, or in a Kubeflow ContainerOp.

It uses Quarkus, the Supersonic Subatomic Java Framework. If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Building the project.

You can build this project using: ``mvn package``

## Running the model from a command line:

You can run this model from a command line using:

```
java -classpath target/ascvd-0.0.1-SNAPSHOT-runner.jar com.ibm.health.analytics.ascvd.interop.AscvdDriverCli \
	-male <<MALE>> \
	-africanAmerican <<AFRICAN_AMERICAN>> \
	-age <<AGE>> \
	-totalCholesterol <<TOTAL_CHOLESTEROL>> \
	-hdlCholesterol <<HDL_CHOLESTEROL>> \
	-systolicBp <<SYSTOLIC_BP>> \
	-bpTreated <<BP_TREATED>> \
	-currentSmoker <<CURRENT_SMOKER>> \
	-diabetic <<DIABETIC>>
```

This will return a JSON object that includes calculation for the `tenYearRisk` of cardiovascular diseease.

## Running the model from a docker container:

You can run this model from a docker container:

```
docker run -p 8080:8080 alvearie/ascvd:0.0.1
```

Once the image is deployed, you can access the ASCVD API using a URI:

```
curl "http://<<HOST_NAME>>:8080/ascvd?\
africanAmerican=<<AFRICAN_AMERICAN>>\
&age=<<AGE>>&\
totalCholesterol=<<TOTAL_CHOLESTEROL>>\
&hdlCholesterol=<<HDL_CHOLESTEROL>>\
&systolicBp=<<SYSTOLIC_BP>>\
&bpTreated=<<BP_TREATED>>\
&currentSmoker=<<CURRENT_SMOKER>>\
&diabetic=<<DIABETIC>>"
```

This will deploy and run the Alvearie build of ASCVD, but you can optionally choose to rebuild/push your own container using:

```
mvn package
```

and then

```
docker push <<GROUP>>/ascvd:0.0.1-SNAPSHOT
```


## Running the model from a kubernetes container.

You can run this model from a kubernetes container by first building the project:

```
mvn package
```

Then deploying the generated kubernetes yaml file:

```
kubectl apply -f target/kubernetes/kubernetes.yml
```

Once deployed you can identify the external IP of your service using: 

```
kubectl get services ascvd
```

You can access the ASCVD API using a URI:

```
curl "http://<<EXTERNAL_IP>>:8080/ascvd?\
africanAmerican=<<AFRICAN_AMERICAN>>\
&age=<<AGE>>&\
totalCholesterol=<<TOTAL_CHOLESTEROL>>\
&hdlCholesterol=<<HDL_CHOLESTEROL>>\
&systolicBp=<<SYSTOLIC_BP>>\
&bpTreated=<<BP_TREATED>>\
&currentSmoker=<<CURRENT_SMOKER>>\
&diabetic=<<DIABETIC>>"
```

This will deploy and run the Alvearie build of ASCVD, but you can optionally choose to rebuild/push your own container using:

```
mvn package
```

and then

```
docker push <<GROUP>>/ascvd:<<PROJECT_VERSION>>
```

where `<<GROUP>>` is set by `quarkus.container-image.group` in `application.properties`, and `<<PROJECT_VERSION>>` is set by the project version (see `pom.xml`).

## Running the model in Kubeflow as a ContainerOp.

You can run this model in Kubeflow using a ContainerOp:

```
    ascvd_containerop = dsl.ContainerOp(
        name='ascvd',
        image='alvearie/ascvd-api:0.0.1',
        arguments=["-outputPath", outputPath, 
                   "-isMale", isMale,
                   "-isAfricanAmerican", isAfricanAmerican,
                   "-age", age,
                   "-totalCholesterol", totalCholesterol,
                   "-hdlCholesterol", hdlCholesterol,
                   "-systolicbp", systolicbp,
                   "-isBpTreated", isBpTreated,
                   "-isCurrentSmoker", isCurrentSmoker,
                   "-isDiabetic", isDiabetic                   
                   ],
        pvolumes={"/mnt": vop.volume}
    ).after(vop)
```

This is just one example of incorporating the container in a kubeflow pipeline.  By passing in the referenced parameters, this will calculate the cardiovascular risk and store it in a JSON at the provided `outputPath` location, in a file named: `ascvd.json`

This container image can be rebuilt by running:

```
docker build -f src/main/docker/Dockerfile.api . -t <<GROUP>/ascvd-api:<<VERSION>>
```

specifying your `<<GROUP>>` and `<<VERSION>>` of choice.

Then push this to a container registry, for example:

```
docker push <<GROUP>>/ascvd-api:<<VERSION>>
```