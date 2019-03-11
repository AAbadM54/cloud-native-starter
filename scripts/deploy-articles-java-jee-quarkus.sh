#!/bin/bash

root_folder=$(cd $(dirname $0); cd ..; pwd)

exec 3>&1

function _out() {
  echo "$(date +'%F %H:%M:%S') $@"
}

function setup() {
  _out Deploying articles-java-jee quarkus
  
  cd ${root_folder}/articles-java-jee
  kubectl delete -f deployment/kubernetes-quarkus.yaml
  kubectl delete -f deployment/istio-quarkus.yaml
  
  eval $(minikube docker-env) 
  mvn -f ${root_folder}/articles-java-jee/pom-quarkus.xml package -Pnative -Dnative-image.docker-build=true

  docker build -f ${root_folder}/articles-java-jee/Dockerfile.quarkus -t articles-quarkus:1 .

  kubectl apply -f deployment/kubernetes-quarkus.yaml
  kubectl apply -f deployment/istio-quarkus.yaml

  minikubeip=$(minikube ip)
  nodeport=$(kubectl get svc articles-quarkus --output 'jsonpath={.spec.ports[*].nodePort}')
  _out Minikube IP: ${minikubeip}
  _out NodePort: ${nodeport}
  
  _out Done deploying articles-java-jee quarkus
  _out Open the OpenAPI explorer: http://${minikubeip}:${nodeport}/openapi/ui/
}

setup