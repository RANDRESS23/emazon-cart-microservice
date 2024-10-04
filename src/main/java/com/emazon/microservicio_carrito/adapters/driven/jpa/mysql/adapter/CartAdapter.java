package com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.adapter;

import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.entity.CartEntity;
import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.mapper.ICartEntityMapper;
import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.repository.ICartRepository;
import com.emazon.microservicio_carrito.domain.model.Cart;
import com.emazon.microservicio_carrito.domain.spi.ICartPersistencePort;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class CartAdapter implements ICartPersistencePort {
    private final ICartRepository cartRepository;
    private final ICartEntityMapper cartEntityMapper;

    @Override
    public Cart saveCart(Cart cart) {
        CartEntity cartEntity = cartRepository.save(cartEntityMapper.toEntity(cart));
        return cartEntityMapper.toDomainModel(cartEntity);
    }

    @Override
    public Optional<Cart> getCartByClientId(Long clientId) {
        return cartRepository.findByClientId(clientId)
            .map(cartEntityMapper::toDomainModel);
    }
}
