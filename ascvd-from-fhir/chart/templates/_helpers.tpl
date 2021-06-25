{{/*
Expand the name of the chart.
*/}}
{{- define "ascvd-from-fhir.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "ascvd-from-fhir.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "ascvd-from-fhir.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "ascvd-from-fhir.labels" -}}
helm.sh/chart: {{ include "ascvd-from-fhir.chart" . }}
{{ include "ascvd-from-fhir.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "ascvd-from-fhir.selectorLabels" -}}
app.kubernetes.io/name: {{ include "ascvd-from-fhir.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "ascvd-from-fhir.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "ascvd-from-fhir.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Create the url ASCVD endpoint to use
*/}}
{{- define "ascvd-from-fhir.ascvd_url" -}}
{{- if .Values.ascvd_url }}
{{- .Values.ascvd_url }}
{{- else }}
{{- printf "http://%s-%s-ascvd:" .Release.Namespace .Release.Name }}{{index .Values.service.port }}
{{- end }}
{{- end }}