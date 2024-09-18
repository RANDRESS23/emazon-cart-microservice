package com.emazon.microservicio_carrito.domain.validation;

import com.emazon.microservicio_carrito.domain.exception.MinCartProductsException;
import com.emazon.microservicio_carrito.domain.model.Cart;
import com.emazon.microservicio_carrito.domain.model.CartProduct;
import com.emazon.microservicio_carrito.domain.util.DomainConstants;

import java.util.List;

public class CartValidation {
    public void validateCart(Cart cart) {
        validateCartProductsProduct(cart.getProducts());
    }

    private void validateCartProductsProduct(List<CartProduct> products) {
        if (products.isEmpty()) {
            throw new MinCartProductsException(DomainConstants.MINIMUM_CART_PRODUCTS_MESSAGE);
        }
    }
}
