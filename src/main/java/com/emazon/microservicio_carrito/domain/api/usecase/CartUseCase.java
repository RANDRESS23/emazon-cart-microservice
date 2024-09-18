package com.emazon.microservicio_carrito.domain.api.usecase;

import com.emazon.microservicio_carrito.domain.api.ICartServicePort;
import com.emazon.microservicio_carrito.domain.exception.AlreadyExistsFieldException;
import com.emazon.microservicio_carrito.domain.model.Cart;
import com.emazon.microservicio_carrito.domain.model.CartProduct;
import com.emazon.microservicio_carrito.domain.spi.IAuthPersistencePort;
import com.emazon.microservicio_carrito.domain.spi.ICartPersistencePort;
import com.emazon.microservicio_carrito.domain.spi.ICartProductPersistencePort;
import com.emazon.microservicio_carrito.domain.util.DomainConstants;
import com.emazon.microservicio_carrito.domain.validation.CartValidation;

import java.time.LocalDateTime;
import java.util.List;

public class CartUseCase implements ICartServicePort {
    private final ICartPersistencePort cartPersistencePort;
    private final ICartProductPersistencePort cartProductPersistencePort;
    private final IAuthPersistencePort authPersistencePort;
    private final CartValidation cartValidation;

    public CartUseCase(ICartPersistencePort cartPersistencePort, ICartProductPersistencePort cartProductPersistencePort, IAuthPersistencePort authPersistencePort, CartValidation cartValidation) {
        this.cartPersistencePort = cartPersistencePort;
        this.cartProductPersistencePort = cartProductPersistencePort;
        this.authPersistencePort = authPersistencePort;
        this.cartValidation = cartValidation;
    }

    @Override
    public Cart saveCart(Cart cart) {
        if (cartPersistencePort.getCartByClientId(cart.getClientId()).isPresent()) {
            throw new AlreadyExistsFieldException(DomainConstants.CART_ALREADY_EXISTS_MESSAGE);
        }

        cartValidation.validateCart(cart);
        Long clientId = authPersistencePort.getAuthenticatedUserId();

        cart.setClientId(clientId);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());

        Cart cartSaved = cartPersistencePort.saveCart(cart);
        List<CartProduct> products = cartProductPersistencePort.getAllProducts(cartSaved.getCartId());
        cartSaved.setProducts(products);

        return cartSaved;
    }

    @Override
    public Cart updateCart(Cart cart) {
        cartValidation.validateCart(cart);
        cart.setUpdatedAt(LocalDateTime.now());

        Cart cartUpdated = cartPersistencePort.saveCart(cart);
        List<CartProduct> products = cartProductPersistencePort.getAllProducts(cartUpdated.getCartId());
        cartUpdated.setProducts(products);

        return cartUpdated;
    }

    @Override
    public Cart getCartByClientId(Long clientId) {
        Cart cart = cartPersistencePort.getCartByClientId(clientId)
                .orElse(null);

        if (cart != null) {
            List<CartProduct> products = cartProductPersistencePort.getAllProducts(cart.getCartId());
            cart.setProducts(products);
        }

        return cart;
    }
}
