package com.emazon.microservicio_carrito.adapters.driving.dto.response;

import com.emazon.microservicio_carrito.domain.model.CartProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
public class CartResponse {
    private final Long cartId;
    private final Long clientId;
    private final Long totalQuantity;
    private final BigDecimal totalPrice;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private List<CartProduct> products;
}