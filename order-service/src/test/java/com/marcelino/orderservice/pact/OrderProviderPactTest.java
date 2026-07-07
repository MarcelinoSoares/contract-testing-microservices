package com.marcelino.orderservice.pact;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider;
import com.marcelino.orderservice.controller.OrderController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Provider Pact Test - Verifica se o order-service cumpre os contratos
 * definidos pelo user-service (Consumer).
 *
 * O contrato e' lido do diretorio 'pacts/' na raiz do projeto.
 * Para usar Pact Broker, troque @PactFolder por @PactBroker.
 */
@WebMvcTest(OrderController.class)
@Provider("order-service")
@PactFolder("../pacts")
public class OrderProviderPactTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup(PactVerificationContext context) {
        context.setTarget(new MockMvcTestTarget(mockMvc));
    }

    // Each @State method documents the precondition the consumer declared.
    // No setup is needed here because OrderController seeds all data in a static
    // in-memory list at startup (ids 1-3, userIds 1-2). If this ever moves to a
    // real repository, replace these no-ops with explicit data-fixture calls.

    @State("order with id 1 exists")
    void orderWithId1Exists() {
        // Covered by static seed: id=1, userId=1, product="Laptop", status="PENDING"
    }

    @State("order with id 999 does not exist")
    void orderWithId999DoesNotExist() {
        // Static list only contains ids 1-3, so id=999 will always return 404
    }

    @State("orders exist for user 1")
    void ordersExistForUser1() {
        // Static seed contains two orders for userId=1 (ids 1 and 2)
    }

    @State("orders exist")
    void ordersExist() {
        // Static seed always contains 3 orders
    }

    @State("order service is available")
    void orderServiceIsAvailable() {
        // No setup required; MockMvcTestTarget initialises the controller directly
    }

    @State("order service rejects invalid payload")
    void orderServiceRejectsInvalidPayload() {
        // No setup required; @Valid on createOrder triggers Spring's constraint
        // validation before the method body runs, returning 400 automatically
    }

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }
}
