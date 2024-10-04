package com.emazon.microservicio_carrito.adapters.driving.service;

import com.emazon.microservicio_carrito.domain.api.ICartServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final ICartServicePort cartServicePort;

    @Transactional
    public void buyCartProducts() {
        cartServicePort.buyCartProducts();
    }
}
