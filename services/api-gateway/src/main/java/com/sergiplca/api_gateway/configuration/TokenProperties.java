package com.sergiplca.api_gateway.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "app.token")
public class TokenProperties {

    private Long expiration;

    private String secret;
}
