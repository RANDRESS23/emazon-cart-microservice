package com.emazon.microservicio_carrito.domain.api.usecase;

import com.emazon.microservicio_carrito.domain.api.ICartServicePort;
import com.emazon.microservicio_carrito.domain.exception.SupplyDateException;
import com.emazon.microservicio_carrito.domain.model.*;
import com.emazon.microservicio_carrito.domain.spi.IAuthPersistencePort;
import com.emazon.microservicio_carrito.domain.spi.ICartProductPersistencePort;
import com.emazon.microservicio_carrito.domain.spi.IStockPersistencePort;
import com.emazon.microservicio_carrito.domain.spi.ITransactionPersistencePort;
import com.emazon.microservicio_carrito.domain.validation.CartProductValidation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartProductUseCaseTest {
    @Mock
    private ICartProductPersistencePort cartProductPersistencePort;

    @Mock
    private IStockPersistencePort stockPersistencePort;

    @Mock
    private ITransactionPersistencePort transactionPersistencePort;

    @Mock
    private ICartServicePort cartServicePort;

    @Mock
    private IAuthPersistencePort authPersistencePort;

    @Mock
    private CartProductValidation cartProductValidation;

    @InjectMocks
    private CartProductUseCase cartProductUseCase;

    @Test
    void testSaveCartProduct_Success() {
        // Arrange
        Long clientId = 1L;
        CartProduct cartProduct = new CartProduct(null, null, 1L, 5L, null, null);
        System.out.println("requestId -> "+cartProduct.getProductId());
        Product product = new Product(1L, 10L, new BigDecimal("100.00"), List.of(1L));
        Cart cart = new Cart(1L, clientId, 0L, BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>());

        when(authPersistencePort.getAuthenticatedUserId()).thenReturn(clientId);
        when(cartServicePort.getCartByClientId(clientId)).thenReturn(cart);
        when(stockPersistencePort.verifyProduct(1L)).thenReturn(product);

        // Act
        cartProductUseCase.saveCartProduct(cartProduct);

        // Assert
        verify(cartProductPersistencePort).saveCartProduct(cartProduct);
    }

    @Test
    void testSaveCartProduct_ExceedsStock() {
        // Arrange
        Long clientId = 1L;
        CartProduct cartProduct = new CartProduct(null, null, 1L, 15L, null, null); // Exceeds stock
        Product product = new Product(1L, 10L, new BigDecimal("100.00"), List.of(1L)); // Only 10 in stock
        Cart cart = new Cart(1L, clientId, 0L, BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>());

        when(authPersistencePort.getAuthenticatedUserId()).thenReturn(clientId);
        when(cartServicePort.getCartByClientId(clientId)).thenReturn(cart);
        when(stockPersistencePort.verifyProduct(1L)).thenReturn(product);

        // Act & Assert
        assertThrows(SupplyDateException.class, () -> cartProductUseCase.saveCartProduct(cartProduct));

        verify(transactionPersistencePort, times(1)).verifySupply(1L); // Verify supply chain for stock shortage
        verify(cartProductPersistencePort, times(0)).saveCartProduct(any(CartProduct.class)); // Should not save
    }

    @Test
    void testCreateCart() {
        // Act
        Cart newCart = cartProductUseCase.createCart(1L);

        // Assert
        assertNotNull(newCart);
        assertEquals(1L, newCart.getClientId());
        assertEquals(0L, newCart.getTotalQuantity());
        assertEquals(BigDecimal.ZERO, newCart.getTotalPrice());
    }
}