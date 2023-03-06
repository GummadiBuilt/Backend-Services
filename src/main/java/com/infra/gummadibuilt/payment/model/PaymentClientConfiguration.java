package com.infra.gummadibuilt.payment.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "payment")
@Component
@Data
public class PaymentClientConfiguration {

    @NotNull
    private String clientKey;

    @NotNull
    private String clientSecret;
}
