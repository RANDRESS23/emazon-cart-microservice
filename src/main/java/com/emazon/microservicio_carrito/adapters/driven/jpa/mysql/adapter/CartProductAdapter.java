package com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.adapter;

import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.entity.CartProductEntity;
import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.mapper.ICartProductEntityMapper;
import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.repository.ICartProductRepository;
import com.emazon.microservicio_carrito.domain.model.CartProduct;
import com.emazon.microservicio_carrito.domain.spi.ICartProductPersistencePort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CartProductAdapter implements ICartProductPersistencePort {
    private final ICartProductRepository cartProductRepository;
    private final ICartProductEntityMapper cartProductEntityMapper;

    @Override
    public CartProduct saveCartProduct(CartProduct cartProduct) {
        CartProductEntity cartProductEntity = cartProductRepository.save(cartProductEntityMapper.toEntity(cartProduct));
        return cartProductEntityMapper.toDomainModel(cartProductEntity);
    }

    @Override
    public void removeCartProduct(CartProduct cartProduct) {
        CartProductEntity cartProductEntity = cartProductEntityMapper.toEntity(cartProduct);
        cartProductRepository.delete(cartProductEntity);
    }

    @Override
    public List<CartProduct> getAllProducts(Long cartId) {
        List<CartProductEntity> listOfCartProductsEntity = cartProductRepository.findAllProducts(cartId);
        return cartProductEntityMapper.toListOfCartProducts(listOfCartProductsEntity);
    }
}
