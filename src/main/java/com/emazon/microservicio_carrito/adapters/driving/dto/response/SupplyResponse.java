package com.emazon.microservicio_carrito.adapters.driving.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class SupplyResponse {
    private final Long supplyId;
    private final Long productId;
    private final LocalDate date;
}
