package com.emazon.microservicio_carrito.domain.api.usecase;

import com.emazon.microservicio_carrito.domain.exception.AlreadyExistsFieldException;
import com.emazon.microservicio_carrito.domain.model.Cart;
import com.emazon.microservicio_carrito.domain.model.CartProduct;
import com.emazon.microservicio_carrito.domain.spi.IAuthPersistencePort;
import com.emazon.microservicio_carrito.domain.spi.ICartPersistencePort;
import com.emazon.microservicio_carrito.domain.spi.ICartProductPersistencePort;
import com.emazon.microservicio_carrito.domain.util.DomainConstants;
import com.emazon.microservicio_carrito.domain.validation.CartValidation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartUseCaseTest {
    @Mock
    private ICartPersistencePort cartPersistencePort;

    @Mock
    private ICartProductPersistencePort cartProductPersistencePort;

    @Mock
    private IAuthPersistencePort authPersistencePort;

    @Mock
    private CartValidation cartValidation;

    @InjectMocks
    private CartUseCase cartUseCase;

    @Test
    void saveCart_shouldSaveCart_whenCartDoesNotExist() {
        // Arrange
        Cart cart = new Cart(1L, 1L, 10L, new BigDecimal("1000"), LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>());
        Cart savedCart = new Cart(1L, 1L, 10L, new BigDecimal("1000"), LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>());

        when(cartPersistencePort.getCartByClientId(1L)).thenReturn(Optional.empty()); // El valor correcto es 1L, que es el cart.getClientId()
        when(authPersistencePort.getAuthenticatedUserId()).thenReturn(10L);  // El id del usuario autenticado sigue siendo 10L
        when(cartPersistencePort.saveCart(any(Cart.class))).thenReturn(savedCart);
        when(cartProductPersistencePort.getAllProducts(savedCart.getCartId())).thenReturn(new ArrayList<>());

        // Act
        Cart result = cartUseCase.saveCart(cart);

        // Assert
        assertEquals(savedCart, result);
        verify(cartPersistencePort, times(1)).saveCart(any(Cart.class));
        verify(cartValidation, times(1)).validateCart(cart);
        verify(authPersistencePort, times(1)).getAuthenticatedUserId();
    }

    @Test
    void saveCart_shouldThrowAlreadyExistsFieldException_whenCartAlreadyExists() {
        // Arrange
        Cart cart = new Cart(1L, 1L, 10L, new BigDecimal("1000"), LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>());

        when(cartPersistencePort.getCartByClientId(1L)).thenReturn(Optional.of(cart));

        // Act & Assert
        AlreadyExistsFieldException exception = assertThrows(AlreadyExistsFieldException.class, () -> {
            cartUseCase.saveCart(cart);
        });

        assertEquals(DomainConstants.CART_ALREADY_EXISTS_MESSAGE, exception.getMessage());
        verify(cartPersistencePort, times(0)).saveCart(any(Cart.class));  // No se debe guardar el carrito
        verify(cartValidation, times(0)).validateCart(cart);
    }

    @Test
    void updateCart_shouldUpdateCart_whenValid() {
        // Arrange
        Cart cart = new Cart(1L, 1L, 10L, new BigDecimal("1000"), LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>());
        Cart updatedCart = new Cart(1L, 1L, 10L, new BigDecimal("1000"), LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>());

        when(cartPersistencePort.saveCart(any(Cart.class))).thenReturn(updatedCart);
        when(cartProductPersistencePort.getAllProducts(updatedCart.getCartId())).thenReturn(new ArrayList<>());

        // Act
        Cart result = cartUseCase.updateCart(cart);

        // Assert
        assertEquals(updatedCart, result);
        verify(cartValidation, times(1)).validateCart(cart);
        verify(cartPersistencePort, times(1)).saveCart(cart);
    }

    @Test
    void getCartByClientId_shouldReturnCartWithProducts_whenCartExists() {
        // Arrange
        Cart cart = new Cart(1L, 1L, 10L, new BigDecimal("1000"), LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>());
        List<CartProduct> products = List.of(new CartProduct(1L, 1L, 1L, 10L, new BigDecimal("100"), new BigDecimal("1000")));

        when(cartPersistencePort.getCartByClientId(1L)).thenReturn(Optional.of(cart));
        when(cartProductPersistencePort.getAllProducts(cart.getCartId())).thenReturn(products);

        // Act
        Cart result = cartUseCase.getCartByClientId(1L);

        // Assert
        assertEquals(cart, result);
        assertEquals(products, result.getProducts());
        verify(cartPersistencePort, times(1)).getCartByClientId(1L);
        verify(cartProductPersistencePort, times(1)).getAllProducts(cart.getCartId());
    }

    @Test
    void getCartByClientId_shouldReturnNull_whenCartDoesNotExist() {
        // Arrange
        when(cartPersistencePort.getCartByClientId(1L)).thenReturn(Optional.empty());

        // Act
        Cart result = cartUseCase.getCartByClientId(1L);

        // Assert
        assertNull(result);
        verify(cartPersistencePort, times(1)).getCartByClientId(1L);
        verify(cartProductPersistencePort, times(0)).getAllProducts(anyLong());  // No debe buscar productos
    }
}