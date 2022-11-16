package com.camel.integration.router;

import com.camel.integration.model.ConfigModel;
import com.camel.integration.processor.ApplicationEventProcessor;
import com.camel.utils.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ChoiceDefinition;
import org.apache.camel.model.RouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@AllArgsConstructor
public class DynamicEventBasedRouter extends RouteBuilder {

    @Autowired
    private ApplicationEventProcessor applicationProcessor;

    @Override
    @SneakyThrows
    public void configure() {
        try {
            String json = new RestTemplate().getForObject("http://localhost:8080/rules", String.class);
            ObjectMapper mapper = new ObjectMapper();
            ConfigModel configModel = mapper.readValue(json, ConfigModel.class);
            List<ConfigModel.Task> tasks = configModel.getTasks();
            RouteDefinition routeDefinition = from("{{app.camel-from}}")
                    .routeId("runtime_route_config")
                    .log("Input: ${body}")
                    .process(applicationProcessor)
                    .log("Output: ${body}");
            ChoiceDefinition choiceDefinition = routeDefinition.choice();
            for (ConfigModel.Task task : tasks) {
                choiceDefinition.when(exchange -> CommonUtils.getKafkaHeader(exchange).equals(task.getTaskName()));
                ConfigModel.KafkaDetails kafkaDetails = task.getKafkaDetails();
                choiceDefinition = choiceDefinition.to("kafka:" + kafkaDetails.getTopic() + "?brokers=" + kafkaDetails.getBootstrapServer() + "");
            }
            choiceDefinition.endChoice();
        } catch (Exception ex) {
            log.error("Error occurred in DynamicEventBasedRouter :: {}", ex.getMessage());
        }
    }
}
