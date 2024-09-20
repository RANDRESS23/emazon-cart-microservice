package com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.mapper;

import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.entity.CartEntity;
import com.emazon.microservicio_carrito.domain.model.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ICartEntityMapper {
    CartEntity toEntity(Cart cart);
    Cart toDomainModel(CartEntity cartEntity);
}
