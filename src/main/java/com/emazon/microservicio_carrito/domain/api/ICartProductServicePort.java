package com.emazon.microservicio_carrito.domain.api;

import com.emazon.microservicio_carrito.domain.model.CartOrSupplyDate;
import com.emazon.microservicio_carrito.domain.model.CartProduct;

public interface ICartProductServicePort {
    CartOrSupplyDate saveCartProduct(CartProduct cartProduct);
}
