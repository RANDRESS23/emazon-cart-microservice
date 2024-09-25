package com.emazon.microservicio_carrito.adapters.driving.controller;

import com.emazon.microservicio_carrito.adapters.driving.dto.request.AddProductToCart;
import com.emazon.microservicio_carrito.adapters.driving.dto.response.*;
import com.emazon.microservicio_carrito.adapters.driving.mapper.ICartProductRequestMapper;
import com.emazon.microservicio_carrito.adapters.driving.mapper.ICartResponseMapper;
import com.emazon.microservicio_carrito.domain.api.ICartProductServicePort;
import com.emazon.microservicio_carrito.domain.api.ICartServicePort;
import com.emazon.microservicio_carrito.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartRestControllerTest {
    @Mock
    private ICartProductServicePort cartProductServicePort;

    @Mock
    private ICartProductRequestMapper cartProductRequestMapper;

    @Mock
    private ICartResponseMapper cartResponseMapper;

    @Mock
    private ICartServicePort cartServicePort;

    @InjectMocks
    private CartRestController cartRestController;

    private AddProductToCart addProductToCart;
    private CartProduct cartProduct;
    private Cart cart;
    private CartResponse cartResponse;
    private CustomPage<CartProductPage> mockCartProductPage;
    private CartDto mockCartDto;

    @BeforeEach
    void setUp() {
        // Inicialización de objetos comunes a los tests
        addProductToCart = new AddProductToCart(1L, 2L);
        cartProduct = new CartProduct(null, null, 1L, "Camiseta", 2L, null, null);
        cartProduct.setUnitPrice(BigDecimal.valueOf(100));
        cartProduct.setTotalPrice(BigDecimal.valueOf(200));

        cart = new Cart(
                1L, // cartId
                1L, // clientId
                2L, // totalQuantity
                BigDecimal.valueOf(200), // totalPrice
                LocalDateTime.now(), // createdAt
                LocalDateTime.now(), // updatedAt
                List.of(cartProduct) // products
        );

        cartResponse = new CartResponse(
                1L, // cartId
                1L, // clientId
                2L, // totalQuantity
                BigDecimal.valueOf(200), // totalPrice
                LocalDateTime.now(), // createdAt
                LocalDateTime.now(), // updatedAt
                List.of(cartProduct) // products
        );

        mockCartProductPage = createMockCartProductPage();
        mockCartDto = createMockCartDto();
    }

    @Test
    void testAddProductToCartSuccess() {
        // Mocks de las llamadas a los servicios y mappers
        when(cartProductRequestMapper.addRequestToCartProduct(addProductToCart)).thenReturn(cartProduct);
        when(cartProductServicePort.saveCartProduct(cartProduct)).thenReturn(cart);
        when(cartResponseMapper.toCartResponse(cart)).thenReturn(cartResponse);

        // Ejecución del método
        ResponseEntity<CartResponse> responseEntity = cartRestController.addProductToCart(addProductToCart);

        // Verificaciones
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(cartResponse, responseEntity.getBody());

        // Verificar que los mocks se llamaron correctamente
        verify(cartProductRequestMapper).addRequestToCartProduct(addProductToCart);
        verify(cartProductServicePort).saveCartProduct(cartProduct);
        verify(cartResponseMapper).toCartResponse(cart);
    }

    @Test
    void testAddProductToCartServiceThrowsException() {
        // Mocks de las llamadas a los servicios y mappers
        when(cartProductRequestMapper.addRequestToCartProduct(addProductToCart)).thenReturn(cartProduct);
        when(cartProductServicePort.saveCartProduct(cartProduct)).thenThrow(new RuntimeException("Service error"));

        // Ejecución del método y captura de la excepción
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartRestController.addProductToCart(addProductToCart);
        });

        // Verificación del mensaje de la excepción
        assertEquals("Service error", exception.getMessage());

        // Verificar que los mocks se llamaron correctamente
        verify(cartProductRequestMapper).addRequestToCartProduct(addProductToCart);
        verify(cartProductServicePort).saveCartProduct(cartProduct);
    }

    @Test
    void removeProductToCart_Success() {
        // Configurar mocks
        when(cartProductRequestMapper.addRequestToCartProduct(any(AddProductToCart.class))).thenReturn(cartProduct);
        when(cartProductServicePort.removeCartProduct(any(CartProduct.class))).thenReturn(cart);
        when(cartResponseMapper.toCartResponse(any(Cart.class))).thenReturn(cartResponse);

        // Ejecutar método
        ResponseEntity<CartResponse> response = cartRestController.removeProductToCart(addProductToCart);

        // Verificar comportamiento
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cartResponse, response.getBody());

        // Verificar que los métodos fueron llamados con los parámetros correctos
        verify(cartProductRequestMapper).addRequestToCartProduct(addProductToCart);
        verify(cartProductServicePort).removeCartProduct(cartProduct);
        verify(cartResponseMapper).toCartResponse(cart);
    }

    @Test
    void removeProductToCart_InvalidProduct_ThrowsException() {
        // Simular una excepción al mapear el producto
        when(cartProductRequestMapper.addRequestToCartProduct(any(AddProductToCart.class)))
                .thenThrow(new IllegalArgumentException("Invalid product data"));

        // Ejecutar y verificar excepción
        try {
            cartRestController.removeProductToCart(addProductToCart);
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid product data", e.getMessage());
        }

        // Verificar que no se llamó a los otros métodos
        verify(cartProductServicePort, never()).removeCartProduct(any(CartProduct.class));
        verify(cartResponseMapper, never()).toCartResponse(any(Cart.class));
    }

    @Test
    void testGetAllProducts_Success() {
        // Simular comportamientos del servicio y mapper
        when(cartProductServicePort.getAllCartProducts(anyInt(), anyInt(), anyBoolean(), anyString(), anyString()))
                .thenReturn(mockCartProductPage);
        when(cartServicePort.getCartByClientId()).thenReturn(cart);
        when(cartResponseMapper.toCartDto(any(Cart.class))).thenReturn(mockCartDto);
        when(cartResponseMapper.toPageProductDto(any(CustomPage.class)))
                .thenReturn(mockCartProductPageToProductDto());

        // Llamada al controlador
        ResponseEntity<ListCartProducts> response = cartRestController.getAllProducts(0, 10, "asc", "Electrónica", "Nike");

        // Verificar la respuesta
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockCartDto, response.getBody().getCart());
        assertNotNull(response.getBody().getProducts());
        assertEquals(1, response.getBody().getProducts().getContent().size());
    }

    // Métodos auxiliares para crear objetos de prueba

    private CustomPage<CartProductPage> createMockCartProductPage() {
        CartProductPage product = new CartProductPage.CartProductPageBuilder().build();
        product.setCartProductId(1L);
        product.setProductId(101L);
        product.setName("Producto Test");
        product.setStockQuantity(100L);
        product.setTotalQuantityInCart(2L);
        product.setUnitPrice(new BigDecimal("50.00"));
        product.setTotalPrice(new BigDecimal("100.00"));
        product.setCategories(Arrays.asList(new Category(1L, "Electrónica")));
        product.setBrand(new Brand(1L, "Nike"));

        CustomPage<CartProductPage> page = new CustomPage<>();
        page.setPageNumber(0);
        page.setPageSize(10);
        page.setTotalElements(1L);
        page.setTotalPages(1);
        page.setContent(List.of(product));

        return page;
    }

    private CartDto createMockCartDto() {
        return new CartDto(1L, 1001L, 2L, new BigDecimal("100.00"), LocalDateTime.now(), LocalDateTime.now());
    }

    private CustomPage<ProductDto> mockCartProductPageToProductDto() {
        ProductDto productDto = new ProductDto(
                1L, 101L, "Producto Test", 100L, null, 2L, new BigDecimal("50.00"), new BigDecimal("100.00"),
                Arrays.asList(new CategoryDto(1L, "Electrónica")), new BrandDto(1L, "Nike"));

        CustomPage<ProductDto> pageDto = new CustomPage<>();
        pageDto.setPageNumber(0);
        pageDto.setPageSize(10);
        pageDto.setTotalElements(1L);
        pageDto.setTotalPages(1);
        pageDto.setContent(List.of(productDto));

        return pageDto;
    }
}