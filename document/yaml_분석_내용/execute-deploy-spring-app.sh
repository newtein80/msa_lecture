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
kubectl apply -f pv-spring-app.yaml

echo "Step 2: Creating PersistentVolumeClaim (PVC)..."
kubectl apply -f pvc-spring-app.yaml --namespace=$NAMESPACE

echo "Step 3: Deploying Spring Boot Application (Deployment)..."
kubectl apply -f deployment-spring-app.yaml --namespace=$NAMESPACE

echo "Step 4: Creating Service for Spring Boot Application..."
kubectl apply -f svc-spring-app.yaml --namespace=$NAMESPACE

echo "All resources have been applied successfully!"

# 상태 확인
echo "Checking resource status in namespace '$NAMESPACE'..."
kubectl get pv
kubectl get pvc --namespace=$NAMESPACE
kubectl get pods --namespace=$NAMESPACE
kubectl get services --namespace=$NAMESPACE