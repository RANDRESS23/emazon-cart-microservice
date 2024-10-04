package com.emazon.microservicio_carrito.domain.spi;

import com.emazon.microservicio_carrito.domain.model.CartProduct;
import com.emazon.microservicio_carrito.domain.model.CustomPage;

import java.util.List;

public interface ICartProductPersistencePort {
    CartProduct saveCartProduct(CartProduct cartProduct);
    void removeCartProduct(CartProduct cartProduct);
    void removeAllCartProducts(Long cartId);
    List<CartProduct> getAllProducts(Long cartId);
    CustomPage<CartProduct> getAllCartProducts(int page, int size, boolean ascending, Long cartId);
}
