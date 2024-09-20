package com.emazon.microservicio_carrito.adapters.driving.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AddProductToCart {
    private final Long productId;
    private final Long quantity;
}
