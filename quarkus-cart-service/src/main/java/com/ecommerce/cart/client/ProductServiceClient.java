package com.ecommerce.cart.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "product-api")
public interface ProductServiceClient {
    @GET
    @Path("/api/products/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    ProductView getProduct(@PathParam("id") String id, @HeaderParam("Authorization") String authorizationHeader);
}
