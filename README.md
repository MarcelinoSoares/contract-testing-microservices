# Contract Testing with Microservices

[![CI](https://github.com/MarcelinoSoares/contract-testing-microservices/actions/workflows/ci.yml/badge.svg)](https://github.com/MarcelinoSoares/contract-testing-microservices/actions/workflows/ci.yml)
[![Coverage](https://img.shields.io/badge/coverage-100%25-brightgreen)](#coverage)
[![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

> Consumer-driven contract testing for Java/Spring Boot microservices using Pact JVM.

This project demonstrates how to validate service integration contracts without relying on fragile end-to-end tests, shared environments, running databases, or full system orchestration.

It includes a consumer service, a provider service, Pact contract generation, provider verification, optional local Pact Broker, JaCoCo coverage gates, and a GitHub Actions pipeline that enforces contract compatibility before merge.

---

## Why this project?

| Problem | Solution |
| --- | --- |
| Microservices break because API expectations are implicit | Consumer-driven contracts make expectations executable |
| End-to-end tests are slow and flaky | Pact verifies service contracts without full environment orchestration |
| Providers change APIs without noticing consumers | Provider verification fails when contracts are broken |
| CI does not catch integration risk early | Contract verification runs as an automated quality gate |
| Coverage drops silently | JaCoCo gates fail the build when coverage thresholds are not met |

---

## Overview

```text
┌─────────────────┐         Pact Contract         ┌──────────────────┐
│   user-service  │  ─────────────────────────►  │  order-service   │
│   (Consumer)    │                               │   (Provider)     │
└─────────────────┘                               └──────────────────┘
```

The consumer defines its expectations for the provider in a Pact JSON contract.
The provider verifies its real API implementation against that contract.

This approach catches breaking API changes earlier, reduces reliance on brittle E2E tests, and improves release confidence in distributed systems.

---

## Architecture

```text
contract-testing-microservices/
├── order-service/              # Provider: Spring Boot REST API
│   ├── src/main/java/.../
│   │   ├── controller/OrderController.java
│   │   └── model/Order.java
│   ├── src/test/java/.../
│   │   └── pact/OrderProviderPactTest.java
│   └── pom.xml
├── user-service/               # Consumer: calls order-service over HTTP
│   ├── src/main/java/.../
│   │   ├── client/OrderClient.java
│   │   └── model/Order.java
│   ├── src/test/java/.../
│   │   └── pact/OrderConsumerPactTest.java
│   └── pom.xml
├── pacts/                      # Versioned Pact contracts used by CI
├── .github/workflows/ci.yml    # Pipeline: consumer → provider → coverage
└── docker-compose.yml          # Optional local Pact Broker
```

---

## Contract Scenarios

Five Pact interactions cover the `order-service` API:

| Method | Endpoint                | Scenario                   |
| ------ | ----------------------- | -------------------------- |
| `GET`  | `/orders`               | List all orders            |
| `GET`  | `/orders/{id}`          | Find order by ID — success |
| `GET`  | `/orders/{id}`          | Order not found — 404      |
| `GET`  | `/orders/user/{userId}` | Find orders by user        |
| `POST` | `/orders`               | Create order — 201         |

---

## Quickstart

### Prerequisites

- Java 17+
- Maven 3.9+

### Run consumer tests and generate contracts

```bash
cd user-service
mvn test -Dtest=OrderConsumerPactTest
```

This generates:

```text
user-service/target/pacts/user-service-order-service.json
```

### Verify provider against the generated contract

```bash
cp user-service/target/pacts/user-service-order-service.json pacts/

cd order-service
mvn test -Dtest=OrderProviderPactTest
```

### Run full build with tests, coverage, and packaging

```bash
cd user-service
mvn verify

cd ../order-service
mvn verify
```

`mvn verify` includes JaCoCo coverage gates. The build fails if line or instruction coverage drops below the configured threshold.

---

## Optional Local Pact Broker

```bash
docker-compose up -d
```

Open:

```text
http://localhost:9292
```

Default credentials:

```text
pact_workshop / pact_workshop
```

---

## Coverage

JaCoCo is configured in both services, excluding `*Application` bootstrap classes.

| Service       | Lines | Instructions |
| ------------- | ----: | -----------: |
| user-service  |  100% |         100% |
| order-service |  100% |         100% |

Coverage gates are intentionally strict in this sample project to demonstrate CI quality enforcement.

---

## CI/CD Pipeline

The GitHub Actions pipeline follows the natural Pact workflow:

```text
consumer job
    └── builds user-service
    └── runs consumer Pact tests
    └── generates Pact contract
    └── uploads contract artifact

provider job
    └── downloads Pact contract artifact
    └── builds order-service
    └── verifies provider against contract
    └── runs coverage gates
```

Pipeline responsibilities:

- Compile both services
- Run consumer contract tests
- Generate Pact contracts
- Upload generated contracts as CI artifacts
- Verify provider implementation against consumer expectations
- Run JaCoCo coverage gates
- Upload JaCoCo HTML reports
- Upload Surefire reports on failure

Triggers:

- `push` to `main` or `develop`
- `pull_request` to `main` or `develop`
- `workflow_dispatch` for manual runs

---

## Quality Engineering Value

This project demonstrates how contract testing helps engineering teams:

- Detect breaking API changes before deployment
- Reduce dependence on slow and flaky end-to-end tests
- Make service expectations explicit and versioned
- Improve release confidence across microservices
- Shift integration risk left into CI/CD
- Strengthen collaboration between consumer and provider teams

Contract testing does not replace all E2E tests — it reduces integration risk earlier and reserves E2E coverage for critical user journeys.

---

## Design Decisions

- Consumer-driven contracts make API expectations explicit and versioned between services.
- Provider verification runs against the real Spring Boot controller layer — no mocks, no running server.
- Pact contracts are shared through CI artifacts to keep the pipeline reproducible without a broker.
- JaCoCo gates are intentionally strict to demonstrate automated quality enforcement in CI.
- Pact Broker is optional for local exploration; CI uses versioned contracts directly.

---

## Tech Stack

| Technology     | Version              |
| -------------- | -------------------- |
| Java           | 17                   |
| Spring Boot    | 3.2.3                |
| Pact JVM       | 4.6.5                |
| JaCoCo         | 0.8.15               |
| JUnit          | 5.x                  |
| Maven          | 3.9.x                |
| Lombok         | 1.18.38              |
| Docker Compose | Optional Pact Broker |

---

## Author

**Marcelino Soares**
Senior SDET / Quality Engineer · Contract Testing · Microservices · Test Automation · CI/CD

- [LinkedIn](https://www.linkedin.com/in/marcelinosoares)
- [GitHub](https://github.com/MarcelinoSoares)

---

## License

MIT License — see [LICENSE](LICENSE) for details.
