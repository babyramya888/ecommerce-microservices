package com.ecommerce.order;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/api/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("customer")
public class OrderResource {
    @Inject JsonWebToken jwt;
    @Inject OrderService orderService;
    @Context HttpHeaders httpHeaders;

    @POST
    public Response checkout() {
        String authHeader = httpHeaders.getHeaderString(HttpHeaders.AUTHORIZATION);
        Order order = orderService.placeOrder(jwt.getSubject(), authHeader, jwt);
        return Response.status(Response.Status.CREATED).entity(order).build();
    }

    @GET
    public List<Order> myOrders() {
        return orderService.findByCustomer(jwt.getSubject());
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") String id) {
        Order order = orderService.findOrThrow(id);
        if (!order.customerId.equals(jwt.getSubject())) {
            throw new WebApplicationException("Not your order", Response.Status.FORBIDDEN);
        }
        return Response.ok(order).build();
    }
}
