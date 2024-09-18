package com.emazon.microservicio_carrito.adapters.driving.mapper;

import com.emazon.microservicio_carrito.adapters.driving.dto.response.ProductResponse;
import com.emazon.microservicio_carrito.domain.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IProductResponseMapper {
    Product toDomainModel(ProductResponse productResponse);
}
