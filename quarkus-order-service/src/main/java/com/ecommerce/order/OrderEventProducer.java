package com.ecommerce.order;

import com.ecommerce.order.event.OrderPlacedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class OrderEventProducer {
    @Inject
    @Channel("order-placed")
    Emitter<OrderPlacedEvent> emitter;

    public void publish(OrderPlacedEvent event) {
        emitter.send(event);
    }
}
