package com.emazon.microservicio_carrito.domain.validation;

import com.emazon.microservicio_carrito.domain.exception.NegativeNotAllowedException;
import com.emazon.microservicio_carrito.domain.model.CartProduct;
import com.emazon.microservicio_carrito.domain.util.DomainConstants;

public class CartProductValidation {
    public void validateCartProduct(CartProduct product) {
        validateQuantityProduct(product.getQuantity());
    }

    private void validateQuantityProduct(Long quantity) {
        if (quantity < DomainConstants.ZERO_CONSTANT) {
            throw new NegativeNotAllowedException(DomainConstants.NEGATIVE_NOT_ALLOWED_EXCEPTION_MESSAGE);
        }
    }
}
