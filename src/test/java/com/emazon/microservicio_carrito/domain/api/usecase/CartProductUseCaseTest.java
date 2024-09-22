package com.emazon.microservicio_carrito.domain.api.usecase;

import com.emazon.microservicio_carrito.domain.api.ICartServicePort;
import com.emazon.microservicio_carrito.domain.exception.NotFoundException;
import com.emazon.microservicio_carrito.domain.exception.SupplyDateException;
import com.emazon.microservicio_carrito.domain.model.*;
import com.emazon.microservicio_carrito.domain.spi.IAuthPersistencePort;
import com.emazon.microservicio_carrito.domain.spi.ICartProductPersistencePort;
import com.emazon.microservicio_carrito.domain.spi.IStockPersistencePort;
import com.emazon.microservicio_carrito.domain.spi.ITransactionPersistencePort;
import com.emazon.microservicio_carrito.domain.util.DomainConstants;
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
        CartProduct cartProduct = new CartProduct(null, null, 1L, 15L, null, null);
        Product product = new Product(1L, 10L, new BigDecimal("100.00"), List.of(1L));
        Cart cart = new Cart(1L, clientId, 0L, BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>());

        when(authPersistencePort.getAuthenticatedUserId()).thenReturn(clientId);
        when(cartServicePort.getCartByClientId(clientId)).thenReturn(cart);
        when(stockPersistencePort.verifyProduct(1L)).thenReturn(product);

        // Act & Assert
        assertThrows(SupplyDateException.class, () -> cartProductUseCase.saveCartProduct(cartProduct));

        verify(transactionPersistencePort, times(1)).verifySupply(1L);
        verify(cartProductPersistencePort, times(0)).saveCartProduct(any(CartProduct.class));
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

    @Test
    void removeCartProduct_CartNotFound() {
        // Arrange
        CartProduct cartProduct = new CartProduct(null, null, 1L, 15L, null, null);

        // Simular el cliente autenticado
        when(authPersistencePort.getAuthenticatedUserId()).thenReturn(1L);

        // Simular que el carrito no existe
        when(cartServicePort.getCartByClientId(1L)).thenReturn(null);

        // Verificar que se lanza NotFoundException
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            cartProductUseCase.removeCartProduct(cartProduct);
        });

        assertEquals(DomainConstants.CART_NOT_FOUND, thrown.getMessage());

        // Verificar que los demás métodos no son llamados
        verify(cartProductPersistencePort, never()).removeCartProduct(any());
    }

    @Test
    void removeCartProduct_ProductNotFoundInCart() {
        // Arrange
        Cart cart = new Cart(1L, 1L, 0L, BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>());

        // Simular el cliente autenticado y un carrito sin el producto
        when(authPersistencePort.getAuthenticatedUserId()).thenReturn(1L);
        when(cartServicePort.getCartByClientId(1L)).thenReturn(cart);

        // Crear un CartProduct diferente que no existe en el carrito
        CartProduct nonExistentProduct = new CartProduct(2L, 1L, 2L, 1L, new BigDecimal("15.00"), new BigDecimal("15.00"));

        // Verificar que se lanza NotFoundException
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            cartProductUseCase.removeCartProduct(nonExistentProduct);
        });

        assertEquals(DomainConstants.CART_PRODUCT_NOT_FOUND, thrown.getMessage());

        // Verificar que el método remove no es llamado
        verify(cartProductPersistencePort, never()).removeCartProduct(any());
    }

    @Test
    void removeCartProduct_SuccessRemoveFullProduct() {
        // Arrange
        Long clientId = 1L;
        CartProduct cartProduct = new CartProduct(1L, 1L, 1L, 5L, new BigDecimal("100.00"), new BigDecimal("500.00")); // precio total del producto es 500
        Product product = new Product(1L, 10L, new BigDecimal("100.00"), List.of(1L));
        List<CartProduct> products = new ArrayList<>();
        products.add(cartProduct);
        Cart cart = new Cart(1L, clientId, 5L, new BigDecimal("500.00"), LocalDateTime.now(), LocalDateTime.now(), products); // precio total del carrito es 500

        // Simular el cliente autenticado y el carrito con el producto
        when(authPersistencePort.getAuthenticatedUserId()).thenReturn(1L);
        when(cartServicePort.getCartByClientId(1L)).thenReturn(cart);
        when(stockPersistencePort.verifyProduct(cartProduct.getProductId())).thenReturn(product);

        // Simular validación exitosa
        doNothing().when(cartProductValidation).validateCartProduct(cartProduct);

        // Mockear la actualización del carrito
        when(cartServicePort.updateCart(any())).thenReturn(cart);

        // Ejecutar el método para eliminar completamente el producto
        Cart updatedCart = cartProductUseCase.removeCartProduct(cartProduct);

        // Verificar que el producto fue eliminado del carrito
        verify(cartProductPersistencePort).removeCartProduct(cartProduct);
        verify(cartServicePort).updateCart(any());

        // Verificar que el total de productos y precio fueron actualizados correctamente
        assertEquals(0L, updatedCart.getTotalQuantity()); // No debería haber productos
        assertEquals(new BigDecimal("0.00"), updatedCart.getTotalPrice()); // El precio total debería ser 0.00
    }
}