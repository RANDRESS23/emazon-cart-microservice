package com.emazon.microservicio_carrito.domain.spi;

import com.emazon.microservicio_carrito.domain.model.Cart;

import java.util.Optional;

public interface ICartPersistencePort {
    Cart saveCart(Cart cart);
    Optional<Cart> getCartByClientId(Long clientId);
}
