package com.emazon.microservicio_carrito.adapters.driving.mapper;

import com.emazon.microservicio_carrito.adapters.driving.dto.response.SupplyResponse;
import com.emazon.microservicio_carrito.domain.model.Supply;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ISupplyResponseMapper {
    Supply toDomainModel(SupplyResponse supplyResponse);
}
