package com.emazon.microservicio_carrito.domain.api;

import com.emazon.microservicio_carrito.domain.model.Cart;
import com.emazon.microservicio_carrito.domain.model.CartProduct;
import com.emazon.microservicio_carrito.domain.model.CartProductPage;
import com.emazon.microservicio_carrito.domain.model.CustomPage;

public interface ICartProductServicePort {
    Cart saveCartProduct(CartProduct cartProduct);
    Cart removeCartProduct(CartProduct cartProduct);
    CustomPage<CartProductPage> getAllCartProducts(Integer page, Integer size, Boolean ascending, String category, String brand);
}
