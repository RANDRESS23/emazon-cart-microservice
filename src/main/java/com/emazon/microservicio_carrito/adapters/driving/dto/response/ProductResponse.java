package com.emazon.microservicio_carrito.adapters.driving.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Getter
public class ProductResponse {
    private final Long productId;
    private final Long quantity;
    private final BigDecimal price;
    private List<Long> categories;
}
