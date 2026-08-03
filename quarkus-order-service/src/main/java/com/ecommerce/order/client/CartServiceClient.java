package com.ecommerce.order.client;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "cart-api")
public interface CartServiceClient {
    @GET
    @Path("/api/cart")
    @Produces(MediaType.APPLICATION_JSON)
    CartView getMyCart(@HeaderParam("Authorization") String authorizationHeader);

    @DELETE
    @Path("/api/cart")
    void clearCart(@HeaderParam("Authorization") String authorizationHeader);
}
