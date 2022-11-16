package com.camel.integration.router;

import com.camel.integration.processor.ApplicationRestProcessor;
import com.camel.utils.CommonUtils;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import static org.apache.camel.model.rest.RestParamType.body;

@Component
public class RestApiBasedRouter extends RouteBuilder {

    @Autowired
    private ApplicationRestProcessor applicationProcessor;

//    @Autowired
//    private HealthCheckService healthCheckService;

    @Autowired
    private Environment env;

    @Override
    public void configure() {
        exposeSwaggerDoc();
        exposeRestEndpoint();

        from("direct:camel")
                .log("Input: ${body}")
                .process(applicationProcessor)
                .log("Output: ${body}")
                .choice()
                .when(exchange -> CommonUtils.getRestHeader(exchange).equals("BOOKING"))
                .to("{{app.camel-to-booking-service}}")
                .when(exchange -> CommonUtils.getRestHeader(exchange).equals("INVOICE"))
                .to("{{app.camel-to-invoice-service}}")
                .end()
                .to("bean:idGeneratorService?method=getId");
    }

    private void exposeRestEndpoint() {
        rest("/refresh/{id}")
                .get()
                .responseMessage().code(200).message("Refreshing rules completed").endResponseMessage()
                .to("bean:refreshRuleService?method=refresh(${header.id})");

//        rest("/actuator/health1")
//                .get()
//                .responseMessage().code(200).message("Refreshing rules completed").endResponseMessage()
//                .to("rest:get:/actuator/mappings");
// //        .to("log:INFO?multiline=true");

        rest("/camel")
                .post().type(Object.class)
                .param().name("body").type(body).description("requestBody").endParam()
                .to("direct:camel")
                .responseMessage().code(200).endResponseMessage();
    }

    private void exposeSwaggerDoc() {
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .enableCORS(true)
                .host("localhost")
                .port(env.getProperty("server.port", "8080"))
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "User API")
                .apiProperty("api.version", "1.0.0");
    }
}

