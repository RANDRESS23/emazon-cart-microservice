package com.emazon.microservicio_carrito.adapters.driving.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartOrSupplyResponse {
    private CartResponse cartResponse;
    private SupplyMessageResponse supplyMessageResponse;

    public CartOrSupplyResponse(CartResponse cartResponse) {
        this.cartResponse = cartResponse;
    }

    public CartOrSupplyResponse(SupplyMessageResponse supplyMessageResponse) {
        this.supplyMessageResponse = supplyMessageResponse;
    }
}
