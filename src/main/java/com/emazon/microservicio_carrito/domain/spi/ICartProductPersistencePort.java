package com.emazon.microservicio_carrito.domain.spi;

import com.emazon.microservicio_carrito.domain.model.CartProduct;

import java.util.List;

public interface ICartProductPersistencePort {
    CartProduct saveCartProduct(CartProduct cartProduct);
    List<CartProduct> getAllProducts(Long cartId);
}
