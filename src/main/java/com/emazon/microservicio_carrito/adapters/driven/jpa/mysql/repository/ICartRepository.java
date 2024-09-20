package com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.repository;

import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICartRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByClientId(Long clientId);
}
