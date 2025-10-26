package br.com.clube_quinze.api.security;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtProperties {

    private String secret;
    private int expirationMinutes;
    private int refreshExpirationDays;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getExpirationMinutes() {
        return expirationMinutes;
    }

    public void setExpirationMinutes(int expirationMinutes) {
        this.expirationMinutes = expirationMinutes;
    }

    public int getRefreshExpirationDays() {
        return refreshExpirationDays;
    }

    public void setRefreshExpirationDays(int refreshExpirationDays) {
        this.refreshExpirationDays = refreshExpirationDays;
    }

    public Duration accessTokenTtl() {
        return Duration.ofMinutes(expirationMinutes);
    }

    public Duration refreshTokenTtl() {
        return Duration.ofDays(refreshExpirationDays);
    }
}
