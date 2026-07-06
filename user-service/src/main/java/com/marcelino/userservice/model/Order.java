package com.marcelino.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Order DTO - Representa a estrutura de dados que o user-service
 * espera receber do order-service. Deve ser compativel com o contrato.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order {

    private Long id;
    private Long userId;
    private String product;
    private Integer quantity;
    private Double totalPrice;
    private String status;
}
