#!/bin/bash

root_folder=$(cd $(dirname $0); cd ..; pwd)

exec 3>&1

function _out() {
  echo "$(date +'%F %H:%M:%S') $@"
}

function setup() {
  _out Deploying web-api-java-jee v1
  
  cd ${root_folder}/web-api-java-jee
  kubectl delete -f deployment/kubernetes-service.yaml
  kubectl delete -f deployment/kubernetes-deployment-v1.yaml
  kubectl delete -f deployment/kubernetes-deployment-v2.yaml
  kubectl delete -f deployment/istio-ingress.yaml
  kubectl delete -f deployment/istio-service-v1.yaml
  kubectl delete -f deployment/istio-service-v1.yaml

  file="${root_folder}/web-api-java-jee/liberty-opentracing-zipkintracer-1.2-sample.zip"
  if [ -f "$file" ]
  then
	  echo "$file found"
  else
	  curl -L -o $file https://github.com/WASdev/sample.opentracing.zipkintracer/releases/download/1.2/liberty-opentracing-zipkintracer-1.2-sample.zip
  fi

  sed 's/10/5/' src/main/java/com/ibm/webapi/business/Service.java > src/main/java/com/ibm/webapi/business/Service2.java
  rm src/main/java/com/ibm/webapi/business/Service.java
  mv src/main/java/com/ibm/webapi/business/Service2.java src/main/java/com/ibm/webapi/business/Service.java
  
  eval $(minikube docker-env) 
  docker build -f Dockerfile.nojava -t web-api:1 .

  kubectl apply -f deployment/kubernetes-service.yaml
  kubectl apply -f deployment/kubernetes-deployment-v1.yaml
  kubectl apply -f deployment/istio-ingress.yaml
  kubectl apply -f deployment/istio-service-v1.yaml

  minikubeip=$(minikube ip)
  nodeport=$(kubectl get svc web-api --output 'jsonpath={.spec.ports[*].nodePort}')
  _out Minikube IP: ${minikubeip}
  _out NodePort: ${nodeport}
  
  _out Done deploying web-api-java-jee v1
  _out Open the OpenAPI explorer: http://${minikubeip}:${nodeport}/openapi/ui/
}

setup