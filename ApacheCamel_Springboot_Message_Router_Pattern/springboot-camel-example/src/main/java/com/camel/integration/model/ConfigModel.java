package com.camel.integration.model;

import lombok.Data;

import java.util.List;

@Data
public class ConfigModel {
    private List<Task> tasks;

    @Data
    public static class Task {
        private String taskName;
        private KafkaDetails kafkaDetails;
    }

    @Data
    public static class KafkaDetails {
        private String bootstrapServer;
        private String topic;

    }
}
