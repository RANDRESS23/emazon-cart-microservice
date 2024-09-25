package com.emazon.microservicio_carrito.adapters.driving.dto.response;

import com.emazon.microservicio_carrito.domain.model.CustomPage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ListCartProducts {
    CartDto cart;
    CustomPage<ProductDto> products;
}
