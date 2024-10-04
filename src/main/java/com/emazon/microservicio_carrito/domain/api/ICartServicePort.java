package com.emazon.microservicio_carrito.domain.api;

import com.emazon.microservicio_carrito.domain.model.Cart;

public interface ICartServicePort {
    Cart saveCart(Cart cart);
    Cart updateCart(Cart cart);
    Cart getCartByClientId();
    void buyCartProducts();
}
