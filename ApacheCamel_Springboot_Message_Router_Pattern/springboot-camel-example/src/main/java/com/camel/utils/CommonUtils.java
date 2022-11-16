package com.camel.utils;

import org.apache.camel.Exchange;
import org.apache.kafka.common.header.internals.RecordHeaders;

public class CommonUtils {

    public static String getRestHeader(Exchange exchange) {
        return (String) exchange.getMessage().getHeaders().get("X-TASK-NAME");
    }

    public static String getKafkaHeader(Exchange exchange) {
        return new String(((RecordHeaders) exchange.getMessage().getHeaders().get("kafka.HEADERS")).headers("X-TASK-NAME").iterator().next().value());
    }
}
