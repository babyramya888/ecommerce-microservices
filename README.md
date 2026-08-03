# Ecommerce Microservices Application

A hybrid microservices e-commerce application with **Quarkus** (Java 21) and **.NET 8** services, deployed on Azure (AKS + ACR).

## Services

| Service | Tech | Port | Description |
|---------|------|------|-------------|
| Auth | Quarkus | 8084 | JWT registration/login with RSA keys |
| Product | Quarkus | 8085 | Product catalog & stock management |
| Cart | Quarkus | 8086 | Shopping cart with circuit breaker |
| Order | Quarkus | 8087 | Order placement with Saga & Outbox patterns |
| Payment | .NET 8 | 8082 | Payment charge/refund |
| Notification | .NET 8 | 8083 | Order confirmation notifications |

## Quick Start (Local with Docker)

```bash
docker compose up -d --build
```

Services: `localhost:8084` through `8087` (Quarkus) and `localhost:8082`/`8083` (.NET)

## Swagger UI

- Quarkus: `http://localhost:8084/q/swagger-ui` (etc.)
- .NET: `http://localhost:8082/swagger` and `http://localhost:8083/swagger`

## Azure Deployment

Run the setup script to provision Azure resources:
```bash
chmod +x infra/azure-setup.sh
./infra/azure-setup.sh
```

Then add these GitHub repository secrets:
- `AZURE_CREDENTIALS` — Output from script
- `ACR_LOGIN_SERVER`, `ACR_USERNAME`, `ACR_PASSWORD` — ACR credentials
- `ACR_NAME` — Your ACR name
- `AKS_CREDENTIALS` — AKS cluster credentials

Merge to `main` to trigger CI/CD pipeline.

## Architecture

```
┌─────────────────┐  ┌──────────────┐  ┌─────────┐
│   Auth (8084)   │  │ Product(8085)│  │Cart(8086)│
│   Quarkus       │  │   Quarkus    │  │ Quarkus │
│   JWT/RSA       │  │   Catalog    │  │  Cart   │
└────────┬────────┘  └──────┬───────┘  └────┬────┘
         │                  │                │
         │     ┌────────────┘                │
         └────→│     Order Service (8087)     │
         │     │   Quarkus • Saga • Outbox  │
         │     └──────┬──────────┬───────────┘
         │            │          │
         │     ┌──────┘          │
         │     ▼                 ▼
         │   Product         Notification(8083)
         │              ┌────────┴──────────┐
         └──────────────┤  .NET 8 • EventGrid│
                    ┌───┤  Notification       │
                    │   └─────────────────────┘
                    │
              Payment (8082)
              ┌──────────────┐
              │ .NET 8       │
              │ Charge/Refund│
              └──────────────┘
```

## Key Patterns

- **JWT Authentication** — RSA-signed tokens from Auth service
- **Saga Pattern** — Stock compensation in Order Service
- **Circuit Breaker** — Retry/Fallback in Cart Service
- **Outbox Pattern** — Event persistence in Order Service
- **Async Notification** — CompletableFuture dispatch to .NET service
