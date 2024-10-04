package com.emazon.microservicio_carrito.configuration;

import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.adapter.*;
import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.mapper.ICartEntityMapper;
import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.mapper.ICartProductEntityMapper;
import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.repository.ICartProductRepository;
import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.repository.ICartRepository;
import com.emazon.microservicio_carrito.adapters.driving.mapper.IProductResponseMapper;
import com.emazon.microservicio_carrito.adapters.driving.mapper.IReportRequestMapper;
import com.emazon.microservicio_carrito.adapters.driving.mapper.ISaleRequestMapper;
import com.emazon.microservicio_carrito.adapters.driving.mapper.ISupplyResponseMapper;
import com.emazon.microservicio_carrito.configuration.feign.IReportFeignClient;
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
    private final ISaleRequestMapper saleRequestMapper;
    private final IReportRequestMapper reportRequestMapper;
    private final ISupplyResponseMapper supplyResponseMapper;
    private final IStockFeignClient stockFeignClient;
    private final ITransactionFeignClient transactionFeignClient;
    private final IReportFeignClient reportFeignClient;
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
        return new TransactionAdapter(transactionFeignClient, supplyResponseMapper, saleRequestMapper);
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
    public IReportPersistencePort reportPersistencePort() {
        return new ReportAdapter(reportFeignClient, reportRequestMapper);
    }

    @Bean
    public ICartServicePort cartServicePort() {
        return new CartUseCase(categoryPersistencePort(), cartProductPersistencePort(), authPersistencePort(), stockPersistencePort(), transactionPersistencePort(), reportPersistencePort());
    }

    @Bean
    public ICartProductPersistencePort cartProductPersistencePort() {
        return new CartProductAdapter(cartProductRepository, cartProductEntityMapper);
    }

    @Bean
    public ICartProductServicePort cartProductServicePort() {
        return new CartProductUseCase(cartProductPersistencePort(), stockPersistencePort(), transactionPersistencePort(), cartServicePort(), authPersistencePort(), cartProductValidation());
    }
}
