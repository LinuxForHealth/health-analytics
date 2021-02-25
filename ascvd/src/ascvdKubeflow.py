import kfp
import kfp.components as comp
import kfp.dsl as dsl
from kfp.components import OutputPath

@dsl.pipeline(
    name='ascvd',
    description='ascvd pipeline'
)
def ascvd_pipeline(
    isMale: bool,
    age: int,
    totalCholesterol: int,
    hdlCholesterol: int,
    systolicbp: int,
    outputPath: '/mnt/output/',
    isAfricanAmerican: bool = False,
    isBpTreated: bool = False,
    isCurrentSmoker: bool = False,
    isDiabetic: bool = False
):

    vop = dsl.VolumeOp(
        name="create_pvc",
        resource_name="my-pvc",
        modes=dsl.VOLUME_MODE_RWO,
        size="5M"
    )
    
    ascvd_native =  dsl.ContainerOp(
        name='ascvd_native',
        image='atclark/ascvd-api:0.0.7',
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

    ascvd_curl = dsl.ContainerOp(
        name='ascvd_curl',
        image='tutum/curl',
        command=['sh', '-c'],
        arguments=['curl "http://b2a4fe50-us-south.lb.appdomain.cloud:8080/ascvd?isAfricanAmerican='+str(isAfricanAmerican)+'&age='+str(age)+'&totalCholesterol='+str(totalCholesterol)+'&hdlCholesterol='+str(hdlCholesterol)+'&systolicBp='+str(systolicbp)+'&isBpTreated='+str(isBpTreated)+'&isCurrentSmoker='+str(isCurrentSmoker)+'&isDiabetic='+str(isDiabetic)+'"'],
        pvolumes={"/mnt": vop.volume}
    ).after(vop)

    ascvd_python = dsl.ContainerOp(
        name='ascvd_python',
        image='alvearie/nifi-setup:0.0.1',
        command=['sh', '-c'],
        arguments=['python -c "import requests; result = requests.get(\'http://b2a4fe50-us-south.lb.appdomain.cloud:8080/ascvd?isAfricanAmerican='+str(isAfricanAmerican)+'&age='+str(age)+'&totalCholesterol='+str(totalCholesterol)+'&hdlCholesterol='+str(hdlCholesterol)+'&systolicBp='+str(systolicbp)+'&isBpTreated='+str(isBpTreated)+'&isCurrentSmoker='+str(isCurrentSmoker)+'&isDiabetic='+str(isDiabetic)+'\'); print(result.json())" | tee /mnt/python.output.json'],
        pvolumes={"/mnt": vop.volume}
    ).after(vop)
    
    #def ls_op():
    ls_op = dsl.ContainerOp(
        name='ls',
        image='busybox',
        command=['sh', '-c'],
        arguments=['ls','/mnt'],
        pvolumes={"/mnt": vop.volume}
    ).after(ascvd_native)

    
if __name__ == '__main__':
    from kfp_tekton.compiler import TektonCompiler
    TektonCompiler().compile(ascvd_pipeline, '../target/ascvd.yaml')