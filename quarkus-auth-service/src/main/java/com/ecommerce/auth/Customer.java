package com.ecommerce.auth;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

import java.util.Set;

@MongoEntity(collection = "customers")
public class Customer extends PanacheMongoEntity {
    public String email;
    public String passwordHash;
    public String fullName;
    public Set<String> roles;

    public Customer() {}

    public Customer(String email, String passwordHash, String fullName, Set<String> roles) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.roles = roles;
    }
}
