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

echo "Step 1: Creating PersistentVolume (PV)..."
kubectl apply -f 2-park-pv-mysql.yaml

echo "Step 2: Creating PersistentVolumeClaim (PVC)..."
kubectl apply -f 3-park-pvc-mysql.yaml --namespace=$NAMESPACE

echo "Step 3: Deploying MySQL Database (Deployment)..."
kubectl apply -f 4-park-deploy-mysql.yaml --namespace=$NAMESPACE

echo "Step 4: Creating Service for MySQL Database..."
kubectl apply -f 5-park-svc-mysql.yaml --namespace=$NAMESPACE

echo "All resources have been applied successfully!"

# 상태 확인
echo "Checking resource status in namespace '$NAMESPACE'..."
# kubectl get pv
# kubectl get pvc --namespace=$NAMESPACE
# kubectl get pods --namespace=$NAMESPACE
# kubectl get services --namespace=$NAMESPACE
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