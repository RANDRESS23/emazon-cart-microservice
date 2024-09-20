package com.emazon.microservicio_carrito.configuration.feign;

import com.emazon.microservicio_carrito.configuration.Constants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Component
public class FeignClientInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = request.getHeader(Constants.AUTHORIZATION_HEADER).substring(7);

        template.header(Constants.AUTHORIZATION_HEADER, Constants.BEARER_HEADER + token);
    }
}
