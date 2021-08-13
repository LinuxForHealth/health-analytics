import json
import os
import requests
from os.path import join
from pyspark.sql import *
from pyspark.sql.functions import explode
from flask import Flask, request
from ascvd_data_extraction import *

# export FLASK_APP=ascvd_from_parquet
# flask run

app = Flask(__name__)
spark = SparkSession.builder.appName("Parquet Spark Session").getOrCreate()

@app.route("/extract", methods=['POST'])
def extract_ascvd_input_from_parquet():

    # Read data from request and store in file so parquet can read it in
    with open('/tmp/request.data', 'wb') as f:
      f.write(request.data)
      f.close()
      df = spark.read.parquet("/tmp/request.data")

    entries = df.withColumn("entry", explode("entry"))
    entries.createOrReplaceTempView("resources")

    rs = spark.sql("SELECT entry.resource FROM resources")
    resource_array = rs.rdd.map(lambda p: p.resource).collect()
    return extract_ascvd_input_data(resource_array)

@app.route("/convert", methods=['POST'])
def convert():
  with open('/tmp/request.data', 'w') as f:
    data = request.data.decode("utf-8")
    f.write(data)
    f.close()

  df = spark.read.option("multiLine", True).json("/tmp/request.data")
  output_dir = "/tmp/output.parquet"
  df.write.mode("overwrite").parquet(output_dir)

  for f in os.listdir(output_dir):
    if f.endswith(".parquet"):
      with open(join(output_dir, f), mode='rb') as file: 
        return file.read()
  raise InvalidUsage('Error converting data', status_code=500)

if __name__ == "__main__":
    port = int(os.environ.get("PORT", 8080))
    app.run(host='0.0.0.0', port=port)