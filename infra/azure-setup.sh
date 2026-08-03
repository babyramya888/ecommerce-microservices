#!/bin/bash
# Azure Infrastructure Setup Script
# Run this from your local machine with Azure CLI installed
# Prerequisites: az CLI installed, logged in to Azure Free subscription

set -e

RG_NAME="ecommerce-rg"
AKS_NAME="ecommerce-aks"
ACR_NAME="ecommerceacr$(openssl rand -hex 3)"
LOCATION="eastus"

echo "=== Creating Azure resources ==="

# 1. Create resource group
echo "[1/4] Creating resource group..."
az group create \
  --name $RG_NAME \
  --location $LOCATION

# 2. Create Azure Container Registry (Free tier)
echo "[2/4] Creating Container Registry..."
az acr create \
  --resource-group $RG_NAME \
  --name $ACR_NAME \
  --sku Basic \
  --admin-enabled true

# Get ACR credentials
ACR_LOGIN_SERVER=$(az acr show --name $ACR_NAME --query "loginServer" -o tsv)
ACR_USERNAME=$(az acr credential show --name $ACR_NAME --query "username" -o tsv)
ACR_PASSWORD=$(az acr credential show --name $ACR_NAME --query "passwords[0].value" -o tsv)

echo "ACR Login Server: $ACR_LOGIN_SERVER"
echo "ACR Username: $ACR_USERNAME"
echo "ACR Password: (hidden)"

# 3. Create AKS cluster (Free tier - 1 node)
echo "[3/4] Creating AKS cluster..."
az aks create \
  --resource-group $RG_NAME \
  --name $AKS_NAME \
  --node-count 1 \
  --node-vm-size Standard_D2s_v7 \
  --generate-ssh-keys \
  --enable-addons monitoring

# 4. Get AKS credentials
echo "[4/4] Getting AKS credentials..."
az aks get-credentials --resource-group $RG_NAME --name $AKS_NAME

# 5. Create Kubernetes secret for MongoDB
echo "=== Creating Kubernetes secrets ==="
kubectl create secret generic mongo-secret \
  --from-literal=connection-string="mongodb://mongodb:27017" \
  --from-literal=ssl-required="false" \
  --dry-run=client -o yaml | kubectl apply -f -

# 6. Output values for GitHub Secrets
echo ""
echo "=== Add these GitHub repository secrets ==="
echo ""

# Azure Service Principal credentials
SP=$(az aks show \
  --name $AKS_NAME \
  --resource-group $RG_NAME \
  --query "identityProfile.kubeletidentity.objectId" -o tsv 2>/dev/null || true)

# Generate a service principal for GitHub Actions
SP_JSON=$(az ad sp create-for-rbac \
  --name "github-actions-sp" \
  --role contributor \
  --scopes "/subscriptions/$(az account show --query id -o tsv)/resourceGroups/$RG_NAME" \
  --sdk-auth 2>/dev/null)

echo "AZURE_CREDENTIALS:"
echo "$SP_JSON"
echo ""

echo "ACR_LOGIN_SERVER: $ACR_LOGIN_SERVER"
echo "ACR_USERNAME: $ACR_USERNAME"
echo "ACR_PASSWORD: $ACR_PASSWORD"
echo "ACR_NAME: $ACR_NAME"
echo ""

# Get AKS credentials for GitHub Actions
AKS_CRED=$(az aks get-credentials \
  --resource-group $RG_NAME \
  --name $AKS_NAME \
  --admin \
  --query "config" -o json 2>/dev/null || true)

echo "To generate AKS_CREDENTIALS for GitHub Actions:"
echo "  curl -ss https://login.microsoftonline.com/{tenant-id}/oauth2/token \
  -d 'grant_type=client_credentials&client_id={app-id}&client_secret={password}&resource=https://management.azure.com/' \
  -H 'Content-Type: application/x-www-form-urlencoded'"

echo ""
echo "=== Resources created successfully ==="
echo "AKS cluster: $AKS_NAME"
echo "ACR: $ACR_NAME"
echo "Resource group: $RG_NAME"
