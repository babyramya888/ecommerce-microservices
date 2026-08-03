package com.ecommerce.order.client;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "notification-api")
public interface NotificationServiceClient {
    @POST
    @Path("/api/notifications/eventgrid")
    @Produces(MediaType.APPLICATION_JSON)
    NotificationResponse sendOrderEvent(NotificationEventRequest request);
}
