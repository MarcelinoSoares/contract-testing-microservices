package com.marcelino.orderservice.pact;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.Provider;
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
@PactFolder("../../pacts")  // Caminho relativo para o diretorio de contratos
public class OrderProviderPactTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup(PactVerificationContext context) {
        context.setTarget(new MockMvcTestTarget(mockMvc));
    }

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }
}
