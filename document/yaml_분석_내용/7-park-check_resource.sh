#!/bin/bash

# Namespace 파라미터 확인
if [ $# -ne 1 ]; then
  echo "Usage: $0 <namespace>"
  exit 1
fi

NAMESPACE=$1

# Namespace 생성 (존재하지 않을 경우)
echo "Check namespace '$NAMESPACE' exists..."
kubectl get namespace $NAMESPACE >/dev/null 2>&1
if [ $? -ne 0 ]; then
  echo "Namespace '$NAMESPACE' not found. You must create a namespace..."
  # kubectl create namespace $NAMESPACE
  exit 1
else
  echo "Namespace '$NAMESPACE' check...OK"
fi

echo "==============================================================================="
echo ">>> Check PV..."
echo "==============================================================================="
kubectl get pv
echo "==============================================================================="
echo ">>> Check PVC..."
echo "==============================================================================="
kubectl get pvc --namespace=$NAMESPACE
echo "==============================================================================="
echo ">>> Check DEPLOY..."
echo "==============================================================================="
kubectl get deploy --namespace=$NAMESPACE
echo "==============================================================================="
echo ">>> Check POD..."
echo "==============================================================================="
kubectl get pods --namespace=$NAMESPACE
echo "==============================================================================="
echo ">>> Check SERVICE..."
echo "==============================================================================="
kubectl get services --namespace=$NAMESPACE
echo "==============================================================================="