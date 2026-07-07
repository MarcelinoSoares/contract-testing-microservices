package com.marcelino.orderservice.controller;

import com.marcelino.orderservice.model.Order;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    // In-memory data for demo purposes
    private static final List<Order> orders = Arrays.asList(
        Order.builder()
            .id(1L).userId(1L).product("Laptop").quantity(1)
            .totalPrice(2500.00).status("PENDING").build(),
        Order.builder()
            .id(2L).userId(1L).product("Mouse").quantity(2)
            .totalPrice(150.00).status("CONFIRMED").build(),
        Order.builder()
            .id(3L).userId(2L).product("Keyboard").quantity(1)
            .totalPrice(300.00).status("SHIPPED").build()
    );

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orders.stream()
            .filter(o -> o.getId().equals(id))
            .findFirst();
        return order.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Long userId) {
        List<Order> userOrders = orders.stream()
            .filter(o -> o.getUserId().equals(userId))
            .toList();
        return ResponseEntity.ok(userOrders);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        order.setId((long) (orders.size() + 1));
        order.setStatus("PENDING");
        return ResponseEntity.status(201).body(order);
    }
}
