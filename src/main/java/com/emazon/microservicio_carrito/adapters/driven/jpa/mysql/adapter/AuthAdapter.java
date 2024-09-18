package com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.adapter;

import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.util.DrivenConstants;
import com.emazon.microservicio_carrito.configuration.securityconfig.jwtconfiguration.JwtService;
import com.emazon.microservicio_carrito.domain.spi.IAuthPersistencePort;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@RequiredArgsConstructor
public class AuthAdapter implements IAuthPersistencePort {
    private final JwtService jwtService;

    @Override
    public Long getAuthenticatedUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = request.getHeader(DrivenConstants.AUTHORIZATION_HEADER).substring(7);

        return jwtService.extractUserId(token);
    }
}
