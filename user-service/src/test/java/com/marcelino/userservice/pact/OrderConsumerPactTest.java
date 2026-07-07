package com.marcelino.userservice.pact;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.marcelino.userservice.client.OrderClient;
import com.marcelino.userservice.model.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Consumer Pact Test - Define e verifica os contratos que o user-service
 * espera do order-service (Provider).
 *
 * Este teste GERA os arquivos .json de contrato em target/pacts/
 */
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "order-service", pactVersion = PactSpecVersion.V3)
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
    public RequestResponsePact getAllOrdersPact(PactDslWithProvider builder) {
        return builder
            .given("orders exist")
            .uponReceiving("a request for all orders")
                .path("/orders")
                .method("GET")
            .willRespondWith()
                .status(200)
                .body(LambdaDsl.newJsonArrayMinLike(1, array ->
                    array.object(obj -> {
                        obj.numberType("id");
                        obj.numberType("userId");
                        obj.stringType("product");
                        obj.numberType("quantity");
                        obj.decimalType("totalPrice");
                        obj.stringType("status");
                    })
                ).build())
            .toPact();
    }

    @Pact(consumer = "user-service", provider = "order-service")
    public RequestResponsePact createOrderPact(PactDslWithProvider builder) {
        return builder
            .given("order service is available")
            .uponReceiving("a request to create an order")
                .path("/orders")
                .method("POST")
                .matchHeader("Content-Type", "application/json.*", "application/json")
                .body(new PactDslJsonBody()
                    .numberType("userId", 1L)
                    .stringType("product", "Laptop")
                    .numberType("quantity", 1)
                    .decimalType("totalPrice", 2500.00)
                )
            .willRespondWith()
                .status(201)
                .body(new PactDslJsonBody()
                    .numberType("id", 4L)
                    .numberType("userId", 1L)
                    .stringType("product", "Laptop")
                    .numberType("quantity", 1)
                    .decimalType("totalPrice", 2500.00)
                    .stringType("status", "PENDING")
                )
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
                .body(LambdaDsl.newJsonArrayMinLike(1, array ->
                    array.object(obj -> {
                        obj.numberType("id");
                        obj.numberType("userId", 1L);
                        obj.stringType("product");
                        obj.numberType("quantity");
                        obj.decimalType("totalPrice");
                        obj.stringType("status");
                    })
                ).build())
            .toPact();
    }

    @Pact(consumer = "user-service", provider = "order-service")
    public RequestResponsePact createOrderInvalidPayloadPact(PactDslWithProvider builder) {
        // Order.product is @JsonInclude(NON_NULL), so a null product is omitted from the
        // serialized body entirely — the provider receives a body with no "product" key,
        // which fails @NotBlank validation and returns 400.
        return builder
            .given("order service rejects invalid payload")
            .uponReceiving("a request to create an order with missing product field")
                .path("/orders")
                .method("POST")
                .matchHeader("Content-Type", "application/json.*", "application/json")
                .body(new PactDslJsonBody()
                    .numberType("userId", 1L)
                    .numberType("quantity", 1)
                    .decimalType("totalPrice", 2500.00)
                )
            .willRespondWith()
                .status(400)
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
    @PactTestFor(pactMethod = "getOrderNotFoundPact")
    void testGetOrderNotFound(MockServer mockServer) {
        OrderClient client = new OrderClient(
            new RestTemplate(), mockServer.getUrl()
        );

        assertThatThrownBy(() -> client.getOrderById(999L))
            .isInstanceOf(HttpClientErrorException.class);
    }

    @Test
    @PactTestFor(pactMethod = "getAllOrdersPact")
    void testGetAllOrders(MockServer mockServer) {
        OrderClient client = new OrderClient(
            new RestTemplate(), mockServer.getUrl()
        );

        List<Order> orders = client.getAllOrders();

        assertThat(orders).isNotNull();
        assertThat(orders).isNotEmpty();
    }

    @Test
    @PactTestFor(pactMethod = "createOrderPact")
    void testCreateOrder(MockServer mockServer) {
        OrderClient client = new OrderClient(
            new RestTemplate(), mockServer.getUrl()
        );

        Order newOrder = Order.builder()
            .userId(1L)
            .product("Laptop")
            .quantity(1)
            .totalPrice(2500.00)
            .build();

        Order created = client.createOrder(newOrder);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo("PENDING");
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

    @Test
    @PactTestFor(pactMethod = "createOrderInvalidPayloadPact")
    void testCreateOrderWithInvalidPayloadReturns400(MockServer mockServer) {
        OrderClient client = new OrderClient(
            new RestTemplate(), mockServer.getUrl()
        );

        Order invalidOrder = Order.builder()
            .userId(1L)
            .product(null)
            .quantity(1)
            .totalPrice(2500.00)
            .build();

        assertThatThrownBy(() -> client.createOrder(invalidOrder))
            .isInstanceOf(HttpClientErrorException.class);
    }
}
