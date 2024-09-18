package com.emazon.microservicio_carrito.adapters.driving.mapper;

import com.emazon.microservicio_carrito.adapters.driving.dto.request.AddProductToCart;
import com.emazon.microservicio_carrito.adapters.driving.util.DrivingConstants;
import com.emazon.microservicio_carrito.domain.model.CartProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ICartProductRequestMapper {
    @Mapping(target = DrivingConstants.CART_PRODUCT_ID, ignore = true)
    @Mapping(target = DrivingConstants.CART_ID, ignore = true)
    CartProduct addRequestToCartProduct(AddProductToCart addProductToCart);
}
