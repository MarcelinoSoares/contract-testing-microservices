# user-service — Pact Consumer

Spring Boot 3 service that calls `order-service` via `OrderClient` (RestTemplate).  
In this demo it acts as the **Consumer** in Consumer-Driven Contract Testing.

## Role in the contract flow

1. `OrderConsumerPactTest` defines every HTTP interaction the client expects from `order-service`.
2. Pact runs those interactions against an embedded mock server and, on success, writes the contract to `target/pacts/user-service-order-service.json`.
3. That file is copied to `pacts/` at the repo root so the provider can verify it.

## Run the consumer tests

```bash
mvn test -Dtest=OrderConsumerPactTest
```

Generated contract: `target/pacts/user-service-order-service.json`

## Key classes

| Class | Purpose |
|---|---|
| `OrderClient` | HTTP client — the code under test |
| `Order` | DTO mirroring the provider's response shape |
| `OrderConsumerPactTest` | Defines contracts and verifies client behaviour |

## Stack

Java 17 · Spring Boot 3.2.3 · Pact JVM 4.6.5 (consumer:junit5) · JUnit 5
