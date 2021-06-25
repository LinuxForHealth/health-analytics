FROM python:3.8-slim-buster

WORKDIR /app
ENV FLASK_APP=ascvd-kf.py

RUN pip3 install flask
RUN pip3 install flask-restful
RUN pip3 install requests
RUN pip3 install kafka-python

RUN pip3 install kfp-tekton

COPY *.py .

CMD [ "python3", "-m" , "flask", "run", "--host=0.0.0.0"]
