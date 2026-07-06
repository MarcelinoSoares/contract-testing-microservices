package com.marcelino.userservice.pact;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.marcelino.userservice.client.OrderClient;
import com.marcelino.userservice.model.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Consumer Pact Test - Define e verifica os contratos que o user-service
 * espera do order-service (Provider).
 *
 * Este teste GERA os arquivos .json de contrato em target/pacts/
 */
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "order-service")
public class OrderConsumerPactTest {

    // =========================================================
    // DEFINICAO DOS CONTRATOS (Pacts)
    // =========================================================

    @Pact(consumer = "user-service", provider = "order-service")
    public RequestResponsePact getOrderByIdPact(PactDslWithProvider builder) {
        return builder
            .given("order with id 1 exists")
            .uponReceiving("a request for order 1")
                .path("/orders/1")
                .method("GET")
            .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                    .numberType("id", 1L)
                    .numberType("userId", 1L)
                    .stringType("product", "Laptop")
                    .numberType("quantity", 1)
                    .decimalType("totalPrice", 2500.00)
                    .stringType("status", "PENDING")
                )
            .toPact();
    }

    @Pact(consumer = "user-service", provider = "order-service")
    public RequestResponsePact getOrderNotFoundPact(PactDslWithProvider builder) {
        return builder
            .given("order with id 999 does not exist")
            .uponReceiving("a request for non-existent order 999")
                .path("/orders/999")
                .method("GET")
            .willRespondWith()
                .status(404)
            .toPact();
    }

    @Pact(consumer = "user-service", provider = "order-service")
    public RequestResponsePact getOrdersByUserIdPact(PactDslWithProvider builder) {
        return builder
            .given("orders exist for user 1")
            .uponReceiving("a request for all orders from user 1")
                .path("/orders/user/1")
                .method("GET")
            .willRespondWith()
                .status(200)
                .body(new au.com.dius.pact.consumer.dsl.PactDslJsonArray()
                    .object()
                        .numberType("id")
                        .numberType("userId", 1L)
                        .stringType("product")
                        .numberType("quantity")
                        .decimalType("totalPrice")
                        .stringType("status")
                    .closeObject()
                )
            .toPact();
    }

    // =========================================================
    // TESTES - Verificam o comportamento do OrderClient
    // =========================================================

    @Test
    @PactTestFor(pactMethod = "getOrderByIdPact")
    void testGetOrderById(MockServer mockServer) {
        OrderClient client = new OrderClient(
            new RestTemplate(), mockServer.getUrl()
        );

        Order order = client.getOrderById(1L);

        assertThat(order).isNotNull();
        assertThat(order.getId()).isEqualTo(1L);
        assertThat(order.getUserId()).isEqualTo(1L);
        assertThat(order.getProduct()).isEqualTo("Laptop");
        assertThat(order.getStatus()).isEqualTo("PENDING");
    }

    @Test
    @PactTestFor(pactMethod = "getOrdersByUserIdPact")
    void testGetOrdersByUserId(MockServer mockServer) {
        OrderClient client = new OrderClient(
            new RestTemplate(), mockServer.getUrl()
        );

        List<Order> orders = client.getOrdersByUserId(1L);

        assertThat(orders).isNotNull();
        assertThat(orders).isNotEmpty();
        assertThat(orders.get(0).getUserId()).isEqualTo(1L);
    }
}
