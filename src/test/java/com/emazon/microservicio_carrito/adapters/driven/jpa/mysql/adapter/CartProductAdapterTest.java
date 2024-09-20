package com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.adapter;

import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.entity.CartProductEntity;
import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.mapper.ICartProductEntityMapper;
import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.repository.ICartProductRepository;
import com.emazon.microservicio_carrito.domain.model.CartProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartProductAdapterTest {
    @Mock
    private ICartProductRepository cartProductRepository;

    @Mock
    private ICartProductEntityMapper cartProductEntityMapper;

    @InjectMocks
    private CartProductAdapter cartProductAdapter;

    @Test
    void saveCartProduct_ShouldReturnSavedCartProduct() {
        // Arrange
        CartProduct cartProduct = new CartProduct(1L, 1L, 1L, 2L, BigDecimal.valueOf(50.0), BigDecimal.valueOf(100.0));
        CartProductEntity cartProductEntity = new CartProductEntity();
        cartProductEntity.setCartProductId(1L);
        cartProductEntity.setCartId(1L);
        cartProductEntity.setProductId(1L);
        cartProductEntity.setQuantity(2L);
        cartProductEntity.setUnitPrice(BigDecimal.valueOf(50.0));
        cartProductEntity.setTotalPrice(BigDecimal.valueOf(100.0));

        when(cartProductEntityMapper.toEntity(cartProduct)).thenReturn(cartProductEntity);
        when(cartProductRepository.save(cartProductEntity)).thenReturn(cartProductEntity);
        when(cartProductEntityMapper.toDomainModel(cartProductEntity)).thenReturn(cartProduct);

        // Act
        CartProduct savedCartProduct = cartProductAdapter.saveCartProduct(cartProduct);

        // Assert
        assertNotNull(savedCartProduct);
        assertEquals(cartProduct.getProductId(), savedCartProduct.getProductId());
        verify(cartProductRepository).save(cartProductEntity);
        verify(cartProductEntityMapper).toEntity(cartProduct);
        verify(cartProductEntityMapper).toDomainModel(cartProductEntity);
    }

    @Test
    void getAllProducts_ShouldReturnListOfCartProducts() {
        // Arrange
        Long cartId = 1L;
        CartProduct cartProduct = new CartProduct(1L, cartId, 1L, 2L, BigDecimal.valueOf(50.0), BigDecimal.valueOf(100.0));
        CartProductEntity cartProductEntity = new CartProductEntity();
        cartProductEntity.setCartProductId(1L);
        cartProductEntity.setCartId(cartId);
        cartProductEntity.setProductId(1L);
        cartProductEntity.setQuantity(2L);
        cartProductEntity.setUnitPrice(BigDecimal.valueOf(50.0));
        cartProductEntity.setTotalPrice(BigDecimal.valueOf(100.0));

        List<CartProductEntity> cartProductEntityList = Collections.singletonList(cartProductEntity);
        List<CartProduct> cartProductList = Collections.singletonList(cartProduct);

        when(cartProductRepository.findAllProducts(cartId)).thenReturn(cartProductEntityList);
        when(cartProductEntityMapper.toListOfCartProducts(cartProductEntityList)).thenReturn(cartProductList);

        // Act
        List<CartProduct> result = cartProductAdapter.getAllProducts(cartId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cartProduct.getProductId(), result.get(0).getProductId());
        verify(cartProductRepository).findAllProducts(cartId);
        verify(cartProductEntityMapper).toListOfCartProducts(cartProductEntityList);
    }

    @Test
    void getAllProducts_ShouldReturnEmptyListWhenNoProductsFound() {
        // Arrange
        Long cartId = 1L;

        when(cartProductRepository.findAllProducts(cartId)).thenReturn(Collections.emptyList());
        when(cartProductEntityMapper.toListOfCartProducts(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act
        List<CartProduct> result = cartProductAdapter.getAllProducts(cartId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cartProductRepository).findAllProducts(cartId);
        verify(cartProductEntityMapper).toListOfCartProducts(Collections.emptyList());
    }
}