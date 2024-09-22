package com.emazon.microservicio_carrito.domain.api;

import com.emazon.microservicio_carrito.domain.model.Cart;
import com.emazon.microservicio_carrito.domain.model.CartProduct;

public interface ICartProductServicePort {
    Cart saveCartProduct(CartProduct cartProduct);
    Cart removeCartProduct(CartProduct cartProduct);
}
