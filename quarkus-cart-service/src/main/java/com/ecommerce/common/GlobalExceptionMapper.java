package com.ecommerce.common;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.Instant;
import java.util.Map;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof WebApplicationException wae) {
            return Response.status(wae.getResponse().getStatus())
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", exception.getMessage(), "status", wae.getResponse().getStatus(), "timestamp", Instant.now().toString()))
                    .build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("error", "Internal server error", "status", 500, "timestamp", Instant.now().toString()))
                .build();
    }
}
