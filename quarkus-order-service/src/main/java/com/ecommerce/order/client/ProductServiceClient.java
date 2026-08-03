package com.ecommerce.order.client;

import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "product-api")
public interface ProductServiceClient {
    @PUT
    @Path("/api/products/{id}/reduce-stock")
    @Produces(MediaType.APPLICATION_JSON)
    ProductView reduceStock(@PathParam("id") String id, @QueryParam("quantity") int quantity, @HeaderParam("Authorization") String authorizationHeader);

    @PUT
    @Path("/api/products/{id}/restore-stock")
    @Produces(MediaType.APPLICATION_JSON)
    ProductView restoreStock(@PathParam("id") String id, @QueryParam("quantity") int quantity, @HeaderParam("Authorization") String authorizationHeader);
}
