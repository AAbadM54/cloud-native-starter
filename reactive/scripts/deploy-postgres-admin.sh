#!/bin/bash

root_folder=$(cd $(dirname $0); cd ..; pwd)

exec 3>&1

function _out() {
  echo "$(date +'%F %H:%M:%S') $@"
}

function setup() {
  _out Deploying postgres admin UI

  eval $(minikube docker-env)

  kubectl create ns demo
  kubectl create -f https://raw.githubusercontent.com/kubedb/cli/0.9.0/docs/examples/postgres/quickstart/pgadmin.yaml

  _out Done deploying postgres admin UI
  _out Wait until the pod has been started: \"kubectl get pods -n demo --watch\"
  _out URL: \"minikube service pgadmin -n demo --url\"
  _out Credentials - user: admin, password: admin
}

setup