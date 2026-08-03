package com.ecommerce.auth;

public class AuthResponse {
    public String token;
    public String tokenType = "Bearer";
    public long expiresInSeconds;
    public String customerId;
    public String email;

    public AuthResponse(String token, long expiresInSeconds, String customerId, String email) {
        this.token = token;
        this.expiresInSeconds = expiresInSeconds;
        this.customerId = customerId;
        this.email = email;
    }
}
