package com.marcelino.userservice.client;

import com.marcelino.userservice.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * OrderClient - HTTP Client do user-service para consumir o order-service.
 * Este e' o componente que o teste Consumer Pact ira' testar.
 */
@Component
public class OrderClient {

    private final RestTemplate restTemplate;
    private final String orderServiceBaseUrl;

    public OrderClient(RestTemplate restTemplate,
                       @Value("${order.service.url:http://localhost:8081}") String orderServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.orderServiceBaseUrl = orderServiceBaseUrl;
    }

    public Order getOrderById(Long id) {
        return restTemplate.getForObject(
            orderServiceBaseUrl + "/orders/" + id,
            Order.class
        );
    }

    public List<Order> getOrdersByUserId(Long userId) {
        ResponseEntity<List<Order>> response = restTemplate.exchange(
            orderServiceBaseUrl + "/orders/user/" + userId,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Order>>() {}
        );
        return response.getBody();
    }

    public List<Order> getAllOrders() {
        ResponseEntity<List<Order>> response = restTemplate.exchange(
            orderServiceBaseUrl + "/orders",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Order>>() {}
        );
        return response.getBody();
    }

    public Order createOrder(Order order) {
        return restTemplate.postForObject(
            orderServiceBaseUrl + "/orders",
            order,
            Order.class
        );
    }
}
