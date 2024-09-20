package com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.adapter;

import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.entity.CartEntity;
import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.mapper.ICartEntityMapper;
import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.repository.ICartRepository;
import com.emazon.microservicio_carrito.domain.model.Cart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartAdapterTest {
    @Mock
    private ICartRepository cartRepository;

    @Mock
    private ICartEntityMapper cartEntityMapper;

    @InjectMocks
    private CartAdapter cartAdapter;

    @Test
    void saveCart_ShouldReturnSavedCart() {
        // Arrange
        Cart cart = new Cart(1L, 1L, 10L, BigDecimal.valueOf(100.0), LocalDateTime.now(), LocalDateTime.now(), Collections.emptyList());
        CartEntity cartEntity = new CartEntity();
        cartEntity.setCartId(1L);
        cartEntity.setClientId(1L);
        cartEntity.setTotalQuantity(10L);
        cartEntity.setTotalPrice(BigDecimal.valueOf(100.0));
        cartEntity.setCreatedAt(LocalDateTime.now());
        cartEntity.setUpdatedAt(LocalDateTime.now());

        when(cartEntityMapper.toEntity(cart)).thenReturn(cartEntity);
        when(cartRepository.save(cartEntity)).thenReturn(cartEntity);
        when(cartEntityMapper.toDomainModel(cartEntity)).thenReturn(cart);

        // Act
        Cart savedCart = cartAdapter.saveCart(cart);

        // Assert
        assertNotNull(savedCart);
        assertEquals(cart.getClientId(), savedCart.getClientId());
        verify(cartRepository).save(cartEntity);
        verify(cartEntityMapper).toEntity(cart);
        verify(cartEntityMapper).toDomainModel(cartEntity);
    }

    @Test
    void getCartByClientId_ShouldReturnCartWhenFound() {
        // Arrange
        Long clientId = 1L;
        Cart cart = new Cart(1L, clientId, 10L, BigDecimal.valueOf(100.0), LocalDateTime.now(), LocalDateTime.now(), Collections.emptyList());
        CartEntity cartEntity = new CartEntity();
        cartEntity.setCartId(1L);
        cartEntity.setClientId(clientId);
        cartEntity.setTotalQuantity(10L);
        cartEntity.setTotalPrice(BigDecimal.valueOf(100.0));
        cartEntity.setCreatedAt(LocalDateTime.now());
        cartEntity.setUpdatedAt(LocalDateTime.now());

        when(cartRepository.findByClientId(clientId)).thenReturn(Optional.of(cartEntity));
        when(cartEntityMapper.toDomainModel(cartEntity)).thenReturn(cart);

        // Act
        Optional<Cart> result = cartAdapter.getCartByClientId(clientId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(cart.getClientId(), result.get().getClientId());
        verify(cartRepository).findByClientId(clientId);
        verify(cartEntityMapper).toDomainModel(cartEntity);
    }

    @Test
    void getCartByClientId_ShouldReturnEmptyWhenNotFound() {
        // Arrange
        Long clientId = 1L;

        when(cartRepository.findByClientId(clientId)).thenReturn(Optional.empty());

        // Act
        Optional<Cart> result = cartAdapter.getCartByClientId(clientId);

        // Assert
        assertFalse(result.isPresent());
        verify(cartRepository).findByClientId(clientId);
        verify(cartEntityMapper, never()).toDomainModel(any());
    }
}