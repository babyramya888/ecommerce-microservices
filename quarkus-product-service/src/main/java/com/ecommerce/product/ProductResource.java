package com.ecommerce.product;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.stream.Collectors;

@Path("/api/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {
    @Inject
    JsonWebToken jwt;

    @GET
    public List<ProductView> listAll() {
        return Product.<Product>listAll().stream().map(ProductView::new).collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") String id) {
        Product product = findOrThrow(id);
        return Response.ok(new ProductView(product)).build();
    }

    @POST
    public Response create(@Valid ProductRequest request) {
        Product product = new Product(request.name, request.description, request.price, request.stockQuantity, request.category);
        product.createdBy = jwt.getSubject();
        product.persist();
        return Response.status(Response.Status.CREATED).entity(new ProductView(product)).build();
    }

    @PUT
    @Path("/{id}/reduce-stock")
    public Response reduceStock(@PathParam("id") String id, @QueryParam("quantity") int quantity) {
        Product product = findOrThrow(id);
        if (product.stockQuantity < quantity) {
            throw new WebApplicationException("Insufficient stock for product " + id, Response.Status.CONFLICT);
        }
        product.stockQuantity -= quantity;
        product.update();
        return Response.ok(new ProductView(product)).build();
    }

    @PUT
    @Path("/{id}/restore-stock")
    public Response restoreStock(@PathParam("id") String id, @QueryParam("quantity") int quantity) {
        Product product = findOrThrow(id);
        product.stockQuantity += quantity;
        product.update();
        return Response.ok(new ProductView(product)).build();
    }

    private Product findOrThrow(String id) {
        if (!ObjectId.isValid(id)) {
            throw new WebApplicationException("Invalid product id", Response.Status.BAD_REQUEST);
        }
        Product product = Product.findById(new ObjectId(id));
        if (product == null) {
            throw new WebApplicationException("Product not found", Response.Status.NOT_FOUND);
        }
        return product;
    }
}
