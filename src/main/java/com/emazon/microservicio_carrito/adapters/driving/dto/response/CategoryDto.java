package com.emazon.microservicio_carrito.adapters.driving.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CategoryDto {
    private final Long categoryId;
    private final String name;
}
