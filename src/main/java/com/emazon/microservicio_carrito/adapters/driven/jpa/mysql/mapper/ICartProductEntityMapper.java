package com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.mapper;

import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.entity.CartProductEntity;
import com.emazon.microservicio_carrito.domain.model.CartProduct;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ICartProductEntityMapper {
    CartProductEntity toEntity(CartProduct cartProduct);
    CartProduct toDomainModel(CartProductEntity cartProductEntity);
    List<CartProduct> toListOfCartProducts(List<CartProductEntity> listOfCartProductsEntity);
}
