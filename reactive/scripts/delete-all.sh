#!/bin/bash

root_folder=$(cd $(dirname $0); cd ..; pwd)

exec 3>&1

function _out() {
  echo "$(date +'%F %H:%M:%S') $@"
}

function setup() {

  cd ${root_folder}/articles-reactive
  kubectl delete -f deployment/kubernetes.yaml --ignore-not-found

}

setup
