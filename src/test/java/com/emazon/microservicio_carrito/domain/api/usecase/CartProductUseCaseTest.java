package com.emazon.microservicio_carrito.domain.api.usecase;

import com.emazon.microservicio_carrito.domain.api.ICartServicePort;
import com.emazon.microservicio_carrito.domain.exception.NotFoundException;
import com.emazon.microservicio_carrito.domain.exception.RemoteServiceException;
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
import java.util.Collections;
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
        CartProduct cartProduct = new CartProduct(null, null, 1L, "camiseta", 5L, null, null);
        Category category = new Category(1L, "ropa");
        Brand brand = new Brand(1L, "nike");
        Product product = new Product(1L, "camiseta", 10L, new BigDecimal("100.00"), List.of(category), brand);
        Cart cart = new Cart(1L, clientId, 0L, BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>());

        when(authPersistencePort.getAuthenticatedUserId()).thenReturn(clientId);
        when(cartServicePort.getCartByClientId()).thenReturn(cart);
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
        CartProduct cartProduct = new CartProduct(null, null, 1L, "camiseta", 15L, null, null);
        Category category = new Category(1L, "ropa");
        Brand brand = new Brand(1L, "nike");
        Product product = new Product(1L, "camiseta", 10L, new BigDecimal("100.00"), List.of(category), brand);
        Cart cart = new Cart(1L, clientId, 0L, BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>());

        when(authPersistencePort.getAuthenticatedUserId()).thenReturn(clientId);
        when(cartServicePort.getCartByClientId()).thenReturn(cart);
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
        CartProduct cartProduct = new CartProduct(null, null, 1L, "camiseta", 15L, null, null);

        // Simular el cliente autenticado
        when(authPersistencePort.getAuthenticatedUserId()).thenReturn(1L);

        // Simular que el carrito no existe
        when(cartServicePort.getCartByClientId()).thenReturn(null);

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
        when(cartServicePort.getCartByClientId()).thenReturn(cart);

        // Crear un CartProduct diferente que no existe en el carrito
        CartProduct nonExistentProduct = new CartProduct(2L, 1L, 2L, "camiseta", 1L, new BigDecimal("15.00"), new BigDecimal("15.00"));

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
        CartProduct cartProduct = new CartProduct(1L, 1L, 1L, "camiseta", 5L, new BigDecimal("100.00"), new BigDecimal("500.00")); // precio total del producto es 500
        Category category = new Category(1L, "ropa");
        Brand brand = new Brand(1L, "nike");
        Product product = new Product(1L, "camiseta", 10L, new BigDecimal("100.00"), List.of(category), brand);
        List<CartProduct> products = new ArrayList<>();
        products.add(cartProduct);
        Cart cart = new Cart(1L, clientId, 5L, new BigDecimal("500.00"), LocalDateTime.now(), LocalDateTime.now(), products); // precio total del carrito es 500

        // Simular el cliente autenticado y el carrito con el producto
        when(authPersistencePort.getAuthenticatedUserId()).thenReturn(1L);
        when(cartServicePort.getCartByClientId()).thenReturn(cart);
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

    @Test
    void testGetAllCartProductsSuccess() {
        // Arrange
        Long cartId = 1L;
        Cart cart = new Cart(cartId, 1L, 10L, new BigDecimal("100.00"), LocalDateTime.now(), LocalDateTime.now(), Collections.emptyList());

        CartProduct cartProduct = new CartProduct(1L, cartId, 1L, "Product 1", 5L, new BigDecimal("10.00"), new BigDecimal("50.00"));
        CustomPage<CartProduct> cartProductPage = new CustomPage<>();
        cartProductPage.setContent(List.of(cartProduct));

        Product product = new Product(1L, "Product 1", 50L, new BigDecimal("10.00"), Collections.emptyList(), new Brand(1L, "Brand 1"));

        // Configurar mocks
        when(cartServicePort.getCartByClientId()).thenReturn(cart);
        when(cartProductPersistencePort.getAllCartProducts(anyInt(), anyInt(), anyBoolean(), eq(cartId))).thenReturn(cartProductPage);
        when(stockPersistencePort.verifyProduct(cartProduct.getProductId())).thenReturn(product);

        // Act
        CustomPage<CartProductPage> result = cartProductUseCase.getAllCartProducts(0, 10, true, "all", "all");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        CartProductPage cartProductPageResult = result.getContent().get(0);
        assertEquals(cartProduct.getCartProductId(), cartProductPageResult.getCartProductId());
        assertEquals(product.getProductId(), cartProductPageResult.getProductId());
        assertEquals(cartProduct.getTotalPrice(), cartProductPageResult.getTotalPrice());

        // Verificar invocaciones a mocks
        verify(cartServicePort).getCartByClientId();
        verify(cartProductPersistencePort).getAllCartProducts(0, 10, true, cartId);
        verify(stockPersistencePort).verifyProduct(cartProduct.getProductId());
    }

    @Test
    void testGetAllCartProductsNotFoundCart() {
        // Arrange
        when(cartServicePort.getCartByClientId()).thenReturn(null);

        // Act & Assert
        assertThrows(RemoteServiceException.class, () -> {
            cartProductUseCase.getAllCartProducts(0, 10, true, "all", "all");
        });

        // Verificar que los otros servicios no fueron llamados
        verify(cartProductPersistencePort, never()).getAllCartProducts(anyInt(), anyInt(), anyBoolean(), anyLong());
        verify(stockPersistencePort, never()).verifyProduct(anyLong());
    }

    @Test
    void testGetAllCartProductsFilterCategory() {
        // Arrange
        Long cartId = 1L;
        Cart cart = new Cart(cartId, 1L, 10L, new BigDecimal("100.00"), LocalDateTime.now(), LocalDateTime.now(), Collections.emptyList());

        CartProduct cartProduct = new CartProduct(1L, cartId, 1L, "Product 1", 5L, new BigDecimal("10.00"), new BigDecimal("50.00"));
        CustomPage<CartProduct> cartProductPage = new CustomPage<>();
        cartProductPage.setContent(List.of(cartProduct));

        Category category = new Category(1L, "Category 1");
        Product product = new Product(1L, "Product 1", 50L, new BigDecimal("10.00"), List.of(category), new Brand(1L, "Brand 1"));

        // Configurar mocks
        when(cartServicePort.getCartByClientId()).thenReturn(cart);
        when(cartProductPersistencePort.getAllCartProducts(anyInt(), anyInt(), anyBoolean(), eq(cartId))).thenReturn(cartProductPage);
        when(stockPersistencePort.verifyProduct(cartProduct.getProductId())).thenReturn(product);

        // Act
        CustomPage<CartProductPage> result = cartProductUseCase.getAllCartProducts(0, 10, true, "Category 1", "all");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Category 1", result.getContent().get(0).getCategories().get(0).getName());

        // Verificar invocaciones a mocks
        verify(cartServicePort).getCartByClientId();
        verify(cartProductPersistencePort).getAllCartProducts(0, 10, true, cartId);
        verify(stockPersistencePort).verifyProduct(cartProduct.getProductId());
    }

    @Test
    void testGetAllCartProductsRemoteServiceException() {
        // Arrange
        when(cartServicePort.getCartByClientId()).thenThrow(new RuntimeException("Unknown error occurred."));

        // Act & Assert
        RemoteServiceException thrown = assertThrows(RemoteServiceException.class, () -> {
            cartProductUseCase.getAllCartProducts(0, 10, true, "all", "all");
        });

        assertEquals("Unknown error occurred.", thrown.getMessage());

        // Verificar que los otros servicios no fueron llamados
        verify(cartProductPersistencePort, never()).getAllCartProducts(anyInt(), anyInt(), anyBoolean(), anyLong());
        verify(stockPersistencePort, never()).verifyProduct(anyLong());
    }
}