package com.emazon.microservicio_carrito.configuration;

import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.adapter.*;
import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.mapper.ICartEntityMapper;
import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.mapper.ICartProductEntityMapper;
import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.repository.ICartProductRepository;
import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.repository.ICartRepository;
import com.emazon.microservicio_carrito.adapters.driving.mapper.IProductResponseMapper;
import com.emazon.microservicio_carrito.adapters.driving.mapper.ISupplyResponseMapper;
import com.emazon.microservicio_carrito.configuration.feign.IStockFeignClient;
import com.emazon.microservicio_carrito.configuration.feign.ITransactionFeignClient;
import com.emazon.microservicio_carrito.configuration.securityconfig.jwtconfiguration.JwtService;
import com.emazon.microservicio_carrito.domain.api.ICartProductServicePort;
import com.emazon.microservicio_carrito.domain.api.ICartServicePort;
import com.emazon.microservicio_carrito.domain.api.usecase.CartProductUseCase;
import com.emazon.microservicio_carrito.domain.api.usecase.CartUseCase;
import com.emazon.microservicio_carrito.domain.spi.*;
import com.emazon.microservicio_carrito.domain.validation.CartProductValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {
    private final ICartRepository cartRepository;
    private final ICartProductRepository cartProductRepository;
    private final ICartEntityMapper cartEntityMapper;
    private final ICartProductEntityMapper cartProductEntityMapper;
    private final IProductResponseMapper productResponseMapper;
    private final ISupplyResponseMapper supplyResponseMapper;
    private final IStockFeignClient stockFeignClient;
    private final ITransactionFeignClient transactionFeignClient;
    private final JwtService jwtService;

    @Bean
    public IAuthPersistencePort authPersistencePort() {
        return new AuthAdapter(jwtService);
    }

    @Bean
    public IStockPersistencePort stockPersistencePort() {
        return new StockAdapter(stockFeignClient, productResponseMapper);
    }

    @Bean
    public ITransactionPersistencePort transactionPersistencePort() {
        return new TransactionAdapter(transactionFeignClient, supplyResponseMapper);
    }

    @Bean
    public CartProductValidation cartProductValidation() {
        return new CartProductValidation();
    }

    @Bean
    public ICartPersistencePort categoryPersistencePort() {
        return new CartAdapter(cartRepository, cartEntityMapper);
    }

    @Bean
    public ICartServicePort cartServicePort() {
        return new CartUseCase(categoryPersistencePort(), cartProductPersistencePort(), authPersistencePort());
    }

    public ICartProductPersistencePort cartProductPersistencePort() {
        return new CartProductAdapter(cartProductRepository, cartProductEntityMapper);
    }

    @Bean
    public ICartProductServicePort cartProductServicePort() {
        return new CartProductUseCase(cartProductPersistencePort(), stockPersistencePort(), transactionPersistencePort(), cartServicePort(), authPersistencePort(), cartProductValidation());
    }
}
