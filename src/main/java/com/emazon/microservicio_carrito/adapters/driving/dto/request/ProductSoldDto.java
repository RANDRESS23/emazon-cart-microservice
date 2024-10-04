package com.emazon.microservicio_carrito.adapters.driving.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class ProductSoldDto {
    private Long productId;
    private String name;
    private Long quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
