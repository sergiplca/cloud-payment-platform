package com.sergiplca.payment_service.configuration.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "app.outbox")
public class OutboxProperties {

    private Integer pageSize;
    private String defaultSortColumn;
}
