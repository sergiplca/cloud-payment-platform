package com.sergiplca.payment_service.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@Slf4j
public class RequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        try {
            log.info("[REQUEST] - {} {}", request.getMethod(), request.getRequestURI());
        } catch (Exception e) {
            log.error("Error in request interceptor pre-handle", e);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

        try {
            log.info("[RESPONSE] - {} - {} {}", response.getStatus(), request.getMethod(), request.getRequestURI());
        }
        catch (Exception e) {
            log.error("Error in request interceptor post-handle", e);
        }
    }
}
