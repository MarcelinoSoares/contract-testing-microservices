package com.marcelino.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long id;
    private Long userId;
    private String product;
    private Integer quantity;
    private Double totalPrice;
    private String status;
}
