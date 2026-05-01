package com.example.bookplan.auth.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
@Component
public class JwtProperties {
    private String secret;
    private Duration accessTokenValidity;
    private Duration refreshTokenValidity;
}
