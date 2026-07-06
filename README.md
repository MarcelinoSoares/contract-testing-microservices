# Contract Testing with Microservices

[![CI](https://github.com/MarcelinoSoares/contract-testing-microservices/actions/workflows/ci.yml/badge.svg)](https://github.com/MarcelinoSoares/contract-testing-microservices/actions/workflows/ci.yml)
[![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Demonstração de **Consumer-Driven Contract Testing** usando [Pact](https://docs.pact.io/) com microsserviços Java/Spring Boot.

## Visão Geral

Este projeto demonstra como implementar Contract Testing entre microsserviços, garantindo que as integrações entre serviços funcionem corretamente sem a necessidade de testes de integração end-to-end caros e frágeis.

```
┌─────────────────┐         Pact Contract         ┌──────────────────┐
│   user-service  │  ─────────────────────────►  │  order-service   │
│   (Consumer)    │  ◄─────────────────────────  │   (Provider)     │
└─────────────────┘                               └──────────────────┘
```

## Arquitetura

```
contract-testing-microservices/
├── order-service/              # Provider (Spring Boot REST API)
│   ├── src/
│   │   ├── main/java/.../
│   │   │   ├── controller/OrderController.java
│   │   │   ├── model/Order.java
│   │   │   └── OrderServiceApplication.java
│   │   └── test/java/.../
│   │       └── pact/OrderProviderPactTest.java
│   └── pom.xml
├── user-service/               # Consumer (consome order-service)
│   ├── src/
│   │   ├── main/java/.../
│   │   │   ├── client/OrderClient.java
│   │   │   ├── model/Order.java
│   │   │   └── UserServiceApplication.java
│   │   └── test/java/.../
│   │       └── pact/OrderConsumerPactTest.java
│   └── pom.xml
├── pacts/                      # Arquivos de contrato gerados
├── .github/workflows/ci.yml    # CI/CD Pipeline
└── docker-compose.yml          # Pact Broker local
```

## Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.2.x | Framework REST |
| Pact JVM | 4.6.x | Contract Testing |
| JUnit 5 | 5.x | Test runner |
| Maven | 3.9.x | Build tool |
| Docker | - | Pact Broker local |

## Conceitos de Contract Testing

### O que é Contract Testing?
Contract Testing verifica se dois serviços (Consumer e Provider) podem se comunicar corretamente. O **contrato** define as expectativas do Consumer sobre o Provider.

### Fluxo Pact
1. **Consumer** escreve um teste definindo as expectativas da API
2. O Pact gera um arquivo `.json` (o contrato)
3. **Provider** verifica seu endpoint real contra o contrato
4. O resultado garante compatibilidade sem integração real

## Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.9+
- Docker (opcional, para Pact Broker)

### 1. Executar testes do Consumer (gera o contrato)
```bash
cd user-service
mvn test -Dtest=OrderConsumerPactTest
```

### 2. Verificar contrato no Provider
```bash
cd order-service
mvn test -Dtest=OrderProviderPactTest
```

### 3. Subir Pact Broker local (opcional)
```bash
docker-compose up -d
# Acesse: http://localhost:9292
```

### 4. Executar tudo
```bash
# Na raiz do projeto
mvn test --projects user-service && mvn test --projects order-service
```

## Estrutura dos Contratos

Os contratos gerados ficam em `user-service/target/pacts/` e têm o formato:

```json
{
  "consumer": { "name": "user-service" },
  "provider": { "name": "order-service" },
  "interactions": [
    {
      "description": "a request for order 1",
      "request": { "method": "GET", "path": "/orders/1" },
      "response": {
        "status": 200,
        "body": { "id": 1, "userId": 1, "product": "Laptop", "status": "PENDING" }
      }
    }
  ]
}
```

## Autor

**Marcelino Soares** - QA Engineer @ Thoughtworks
- LinkedIn: [marcelinosoares](https://www.linkedin.com/in/marcelinosoares)
- GitHub: [@MarcelinoSoares](https://github.com/MarcelinoSoares)

## Licença

MIT License - veja [LICENSE](LICENSE) para detalhes.
