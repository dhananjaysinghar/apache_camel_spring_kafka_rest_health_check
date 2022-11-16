//package com.camel.integration.router;
//
//import com.camel.integration.processor.ApplicationEventProcessor;
//import com.camel.utils.CommonUtils;
//import org.apache.camel.builder.RouteBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class EventBasedRouter extends RouteBuilder {
//
//    @Autowired
//    private ApplicationEventProcessor applicationProcessor;
//
//    @Override
//    public void configure() {
//        from("{{app.camel-from}}")
//                .log("Input: ${body}")
//                .process(applicationProcessor)
//                .log("Output: ${body}")
//                .choice()
//                .when(exchange -> CommonUtils.getKafkaHeader(exchange).equals("BOOKING"))
//                .to("{{app.camel-to-booking-service}}")
//                .when(exchange -> CommonUtils.getKafkaHeader(exchange).equals("INVOICE"))
//                .to("{{app.camel-to-invoice-service}}")
//                .endChoice();
//    }
//}
