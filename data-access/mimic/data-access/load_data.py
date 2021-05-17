'''
Created on May 12, 2021

@author: atclark
'''

import zipfile
import requests
import os
import pandas as pd
import numpy as np
import csv
from io import BytesIO

def download_mimic(data_dir='./mimic_data'):
    try:
        os.makedirs(data_dir)
    except OSError as exc: # Guard against race condition
        if exc.errno != errno.EEXIST:
            raise

    mimic_1_4_url = 'https://physionet.org/static/published-projects/mimiciii-demo/mimic-iii-clinical-database-demo-1.4.zip'
    print("Downloading Demo Mimic DB from: " + mimic_1_4_url)
    response = requests.get(mimic_1_4_url)
    filebytes = BytesIO(response.content)
    myzipfile = zipfile.ZipFile(filebytes)

    for name in myzipfile.namelist():
        if (not name.endswith('/')):
            data = myzipfile.read(name).decode('UTF-8')
            target_file = os.path.join(data_dir, name[name.rindex('/') + 1 : ])
            f = open(target_file, 'w')
            f.write(data)
            f.close()
   
   
def load_mimic(data_dir='./mimic_data'): 
    mimic_data = {}
    
    #if data hasn't been downloaded retrieve now
    if (not os.path.isdir(data_dir)):
        download_mimic(data_dir)
    
    # for each downloaded file, load into dictionary
    for f in os.listdir(data_dir):
        if (f.endswith(".csv")):
            print(f)
            # csv_data = csv.reader(os.path.join(data_dir, f))
            # mimic_data[f[:f.rindex(".")]] = csv_data
            df = pd.read_csv(os.path.join(data_dir, f))
            mimic_data[f[:f.rindex(".")]] = df

    return mimic_data
    
if __name__ == '__main__':
    mimic_data = load_mimic()
    print("\n\n\nData Loaded...\n\n\n")
    for key, value in mimic_data.items():
        print("\n" + key + ": " + str(value.head()))