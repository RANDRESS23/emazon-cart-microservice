package com.emazon.microservicio_carrito.adapters.driving.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class SupplyMessageResponse {
    private final String message;
    private final LocalDate nextSupplyDate;
}
