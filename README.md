# Contract Testing with Microservices

[![CI](https://github.com/MarcelinoSoares/contract-testing-microservices/actions/workflows/ci.yml/badge.svg)](https://github.com/MarcelinoSoares/contract-testing-microservices/actions/workflows/ci.yml)
[![Coverage](https://img.shields.io/badge/coverage-100%25-brightgreen)](#cobertura)
[![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Demonstração de **Consumer-Driven Contract Testing** usando [Pact JVM](https://docs.pact.io/) com microsserviços Java/Spring Boot.

## Visão Geral

```
┌─────────────────┐         Pact Contract         ┌──────────────────┐
│   user-service  │  ─────────────────────────►  │  order-service   │
│   (Consumer)    │  ◄─────────────────────────  │   (Provider)     │
└─────────────────┘                               └──────────────────┘
```

O **consumer** define o que espera do provider num arquivo JSON de contrato. O **provider** verifica seu código real contra esse contrato — sem servidor rodando, sem banco, sem testes end-to-end frágeis.

## Arquitetura

```
contract-testing-microservices/
├── order-service/              # Provider (Spring Boot REST API)
│   ├── src/main/java/.../
│   │   ├── controller/OrderController.java
│   │   └── model/Order.java
│   ├── src/test/java/.../
│   │   └── pact/OrderProviderPactTest.java
│   └── pom.xml
├── user-service/               # Consumer (consome order-service via HTTP)
│   ├── src/main/java/.../
│   │   ├── client/OrderClient.java
│   │   └── model/Order.java
│   ├── src/test/java/.../
│   │   └── pact/OrderConsumerPactTest.java
│   └── pom.xml
├── pacts/                      # Contratos commitados (fonte da verdade para o CI)
├── .github/workflows/ci.yml    # Pipeline: consumer → provider → cobertura
└── docker-compose.yml          # Pact Broker local (opcional)
```

## Contratos Pact

Cinco interações cobrindo toda a API do `order-service`:

| Método | Endpoint | Cenário |
|--------|----------|---------|
| `GET` | `/orders` | Lista todos os pedidos |
| `GET` | `/orders/{id}` | Busca pedido por ID (200) |
| `GET` | `/orders/{id}` | Pedido não encontrado (404) |
| `GET` | `/orders/user/{userId}` | Pedidos de um usuário |
| `POST` | `/orders` | Cria um pedido (201) |

## Como Executar

### Pré-requisitos

- Java 17+
- Maven 3.9+

### Testes do consumer (gera os contratos)

```bash
cd user-service
mvn test -Dtest=OrderConsumerPactTest
```

O arquivo `user-service/target/pacts/user-service-order-service.json` é gerado ao final.

### Verificação do provider

```bash
# Copie o contrato gerado para a pasta compartilhada
cp user-service/target/pacts/user-service-order-service.json pacts/

cd order-service
mvn test -Dtest=OrderProviderPactTest
```

### Build completo (testes + cobertura + pacote)

```bash
cd user-service && mvn verify
# Em seguida:
cd ../order-service && mvn verify
```

`mvn verify` inclui o gate de cobertura JaCoCo — falha se LINE ou INSTRUCTION cair abaixo de 100%.

### Pact Broker local (opcional)

```bash
docker-compose up -d
# Acesse: http://localhost:9292 (usuário/senha: pact_workshop)
```

## Cobertura

JaCoCo configurado em ambos os serviços com exclusão das classes `*Application`:

| Serviço       | Linhas | Instruções |
| ------------- | ------ | ---------- |
| user-service  | 100%   | 100%       |
| order-service | 100%   | 100%       |

O gate é aplicado no `mvn verify` — qualquer PR que reduza a cobertura falha o CI antes do merge.

## CI/CD

O pipeline segue a ordem natural do Pact:

```text
consumer (mvn verify)
    └─ gera pacts/
provider (mvn verify)   ← depende do artifact acima
    └─ verifica contratos
```

Etapas por job:

- Compilação, testes, gate de cobertura e empacotamento (`mvn verify`)
- Upload do contrato gerado como artifact entre jobs
- Upload dos relatórios JaCoCo HTML (retidos por 7 dias)
- Upload dos relatórios Surefire em caso de falha

Triggers: `push` em `main`/`develop`, `pull_request` em `main`/`develop`, e `workflow_dispatch` para execução manual.

## Stack

| Tecnologia  | Versão                  |
| ----------- | ----------------------- |
| Java        | 17 (compatível com 25+) |
| Spring Boot | 3.2.3                   |
| Pact JVM    | 4.6.5                   |
| JaCoCo      | 0.8.15                  |
| JUnit 5     | 5.x                     |
| Maven       | 3.9.x                   |
| Lombok      | 1.18.38                 |

## Autor

**Marcelino Soares** — QA Engineer @ Thoughtworks
- LinkedIn: [marcelinosoares](https://www.linkedin.com/in/marcelinosoares)
- GitHub: [@MarcelinoSoares](https://github.com/MarcelinoSoares)

## Licença

MIT License — veja [LICENSE](LICENSE) para detalhes.
