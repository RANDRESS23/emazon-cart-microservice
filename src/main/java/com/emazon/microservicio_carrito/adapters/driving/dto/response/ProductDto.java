package com.emazon.microservicio_carrito.adapters.driving.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
public class ProductDto {
    private Long cartProductId;
    private final Long productId;
    private final String name;
    private final Long stockQuantity;
    private final LocalDate nextSupplyDate;
    private final Long totalQuantityInCart;
    private final BigDecimal unitPrice;
    private final BigDecimal totalPrice;
    private List<CategoryDto> categories;
    private BrandDto brand;
}
