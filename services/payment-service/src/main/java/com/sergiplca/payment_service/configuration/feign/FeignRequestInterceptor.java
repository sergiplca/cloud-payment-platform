package com.sergiplca.payment_service.configuration.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeignRequestInterceptor implements RequestInterceptor {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    private HttpServletRequest request;

    @Autowired
    public FeignRequestInterceptor(HttpServletRequest request) {

        this.request = request;
    }

    @Override
    public void apply(RequestTemplate template) {

        template.header(CORRELATION_ID_HEADER, request.getHeader(CORRELATION_ID_HEADER));
    }
}
