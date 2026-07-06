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

    @State("order with id 1 exists")
    void orderWithId1Exists() {}

    @State("order with id 999 does not exist")
    void orderWithId999DoesNotExist() {}

    @State("orders exist for user 1")
    void ordersExistForUser1() {}

    @State("orders exist")
    void ordersExist() {}

    @State("order service is available")
    void orderServiceIsAvailable() {}

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }
}
