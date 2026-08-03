package com.ecommerce.cart;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/api/cart")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("customer")
public class CartResource {
    @Inject JsonWebToken jwt;
    @Inject CartService cartService;
    @Context HttpHeaders httpHeaders;

    @POST
    @Path("/items")
    public Response addItem(@Valid AddItemRequest request) {
        String authHeader = httpHeaders.getHeaderString(HttpHeaders.AUTHORIZATION);
        return Response.ok(cartService.addItem(jwt.getSubject(), request, authHeader)).build();
    }

    @GET
    public Response getCartItems() {
        return Response.ok(cartService.getOrCreateCart(jwt.getSubject())).build();
    }

    @DELETE
    @Path("/items/{productId}")
    public Response removeItem(@PathParam("productId") String productId) {
        return Response.ok(cartService.removeItem(jwt.getSubject(), productId)).build();
    }

    @DELETE
    public Response clearCart() {
        cartService.clearCart(jwt.getSubject());
        return Response.noContent().build();
    }
}
