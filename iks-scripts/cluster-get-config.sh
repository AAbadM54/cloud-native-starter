#!/bin/bash

root_folder=$(cd $(dirname $0); cd ..; pwd)

function _out() {
  echo "$(date +'%F %H:%M:%S') $@"
}

function _err() {
  echo "$@" >&4
  echo "$(date +'%F %H:%M:%S') $@"
}

readonly CFG_FILE=${root_folder}/local.env
# Check if config file exists, in this case it will have been modified
if [ ! -f $CFG_FILE ]; then
     _out Config file local.env is missing!
     _out -- Copy template.local.env to local.env and edit according to our instructions!
     exit 1
fi  
source $CFG_FILE


function test_cluster() {
  _out Logging into IBM Cloud
  ibmcloud config --check-version=false
  ibmcloud login --apikey $IBMCLOUD_API_KEY -r $IBM_CLOUD_REGION
  _out Check if Kubernetes Cluster is available ...
  STATE=$(ibmcloud ks cluster get --cluster $CLUSTER_NAME -s | awk '/^State:/ {print $2}')
  if [ $STATE != "normal" ]; then 
    _out -- Your Kubernetes cluster is in $STATE state and not ready
    _out ---- Please wait a few more minutes and then try this command again 
    exit 1
   else
    _out Retrieving kubectl config for IKS cluster
    ibmcloud ks cluster config --cluster $CLUSTER_NAME
    _out -- Cluster $CLUSTER_NAME is ready for Istio installation
  fi
}

test_cluster

