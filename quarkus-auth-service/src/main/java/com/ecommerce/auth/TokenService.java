package com.ecommerce.auth;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;

@ApplicationScoped
public class TokenService {
    @ConfigProperty(name = "ecommerce.jwt.expiration-seconds", defaultValue = "3600")
    long expirationSeconds;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    public String generateToken(Customer customer) {
        return Jwt.issuer(issuer)
                .subject(customer.id.toString())
                .claim("email", customer.email)
                .claim("name", customer.fullName)
                .groups(customer.roles)
                .expiresIn(Duration.ofSeconds(expirationSeconds))
                .sign();
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }
}
