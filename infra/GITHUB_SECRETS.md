# GitHub Secrets Setup Guide

Add these secrets to your GitHub repository (`Settings` → `Secrets and variables` → `Actions`):

## Required Secrets

| Name | Value |
|------|-------|
| `AZURE_CREDENTIALS` | Service Principal JSON (below) |
| `ACR_LOGIN_SERVER` | `ecommerceacrdec4e5.azurecr.io` |
| `ACR_USERNAME` | `ecommerceacrdec4e5` |
| `ACR_PASSWORD` | (see below) |
| `ACR_NAME` | `ecommerceacrdec4e5` |
| `AKS_CREDENTIALS` | AKS Cluster credentials JSON (below) |

## AZURE_CREDENTIALS (JSON) — Add via GitHub UI → Settings → Secrets and variables → Actions

Get it by running:
```bash
az ad sp create-for-rbac --name "github-actions-sp" --role contributor --scopes /subscriptions/{sub-id}/resourceGroups/ecommerce-rg --sdk-auth
```

Then add the JSON output as the `AZURE_CREDENTIALS` secret in GitHub.

## AKS_CREDENTIALS (JSON) — Add via GitHub UI

Get it from the AKS cluster:
```bash
CLUSTER_ID=$(az aks show --name ecommerce-aks --resource-group ecommerce-rg --query "id" -o tsv)
SUB_ID=$(az account show --query "id" -o tsv)
TENANT_ID=$(az account show --query "tenantId" -o tsv)
echo "{\"clusterId\": \"$CLUSTER_ID\", \"clusterName\": \"ecommerce-aks\", \"resourceGroup\": \"ecommerce-rg\", \"subscriptionId\": \"$SUB_ID\", \"tenantId\": \"$TENANT_ID\"}"
```

## ACR_PASSWORD
Get it by running:
```bash
az acr credential show --name ecommerceacrdec4e5 --query "passwords[0].value" -o tsv
```

## Azure Resources Created
- Resource Group: `ecommerce-rg` (eastus)
- ACR: `ecommerceacrdec4e5` (Basic tier)
- AKS: `ecommerce-aks` (1x Standard_D2s_v7 node, admin credentials enabled)
- MongoDB and JWT key Kubernetes secrets already created in AKS
