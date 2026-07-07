# order-service — Pact Provider

Spring Boot 3 service exposing a REST API for orders (`/orders`, `/orders/{id}`, `/orders/user/{userId}`).  
In this demo it acts as the **Provider** in Consumer-Driven Contract Testing.

## Role in the contract flow

1. `OrderProviderPactTest` reads the contract from `pacts/` at the repo root.
2. Pact replays each interaction against the real `OrderController` via `MockMvcTestTarget` (no live server).
3. If every interaction matches, the provider is considered verified.

## Run the provider verification

```bash
mvn test -Dtest=OrderProviderPactTest
```

The contract file must exist at `../pacts/user-service-order-service.json` relative to this module (i.e. the repo-root `pacts/` directory).

## Key classes

| Class | Purpose |
|---|---|
| `OrderController` | REST API — the code under verification |
| `Order` | Domain model with Bean Validation constraints (`@NotBlank`, `@NotNull`, `@Positive`) |
| `OrderProviderPactTest` | Loads contracts, configures `MockMvc`, and runs Pact verification |

## Validation

`POST /orders` validates the request body via `@Valid`. A payload with a null or blank `product`, or a non-positive `quantity`/`totalPrice`, returns **400 Bad Request** — this behaviour is covered by the consumer contract `createOrderInvalidPayloadPact`.

## Stack

Java 17 · Spring Boot 3.2.3 · Pact JVM 4.6.5 (provider:junit5spring) · JUnit 5 · Bean Validation (Jakarta)
