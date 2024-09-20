package com.emazon.microservicio_carrito.adapters.driving.mapper;

import com.emazon.microservicio_carrito.adapters.driving.dto.response.CartResponse;
import com.emazon.microservicio_carrito.domain.model.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ICartResponseMapper {
    CartResponse toCartResponse(Cart cart);
}
