package com.ecommerce.order;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "outbox_events")
public class OutboxEvent extends PanacheMongoEntity {
    public String aggregateId;
    public String aggregateType;
    public String eventType;
    public String payload;

    public OutboxEvent() {}

    public OutboxEvent(String aggregateId, String aggregateType, String eventType, String payload) {
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.payload = payload;
    }
}
