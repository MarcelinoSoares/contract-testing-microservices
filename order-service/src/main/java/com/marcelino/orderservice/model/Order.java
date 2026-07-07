package com.marcelino.orderservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotNull
    private Long userId;

    @NotBlank
    private String product;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    @Positive
    private Double totalPrice;

    private String status;
}
