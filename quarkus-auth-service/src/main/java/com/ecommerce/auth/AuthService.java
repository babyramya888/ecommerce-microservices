package com.ecommerce.auth;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.Set;

@ApplicationScoped
public class AuthService {
    @Inject
    TokenService tokenService;

    public AuthResponse register(RegisterRequest request) {
        if (Customer.find("email", request.email).firstResultOptional().isPresent()) {
            throw new WebApplicationException("Email already registered", Response.Status.CONFLICT);
        }

        String hashed = BcryptUtil.bcryptHash(request.password);
        Customer customer = new Customer(request.email, hashed, request.fullName, Set.of("customer"));
        customer.persist();

        String token = tokenService.generateToken(customer);
        return new AuthResponse(token, tokenService.getExpirationSeconds(), customer.id.toString(), customer.email);
    }

    public AuthResponse login(LoginRequest request) {
        Customer customer = Customer.<Customer>find("email", request.email)
                .firstResultOptional()
                .orElseThrow(() -> new WebApplicationException("Invalid email or password", Response.Status.UNAUTHORIZED));

        if (!BcryptUtil.matches(request.password, customer.passwordHash)) {
            throw new WebApplicationException("Invalid email or password", Response.Status.UNAUTHORIZED);
        }

        String token = tokenService.generateToken(customer);
        return new AuthResponse(token, tokenService.getExpirationSeconds(), customer.id.toString(), customer.email);
    }
}
