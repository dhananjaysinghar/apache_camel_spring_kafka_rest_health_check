package com.camel.integration.service;

import com.camel.integration.processor.ApplicationEventProcessor;
import com.camel.integration.router.DynamicEventBasedRouter;
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("refreshRuleService")
public class RefreshRuleService {

    @Autowired
    private ApplicationEventProcessor applicationEventProcessor;
    @Autowired
    private CamelContext camelContext;

    @SneakyThrows
    public void refresh(String id) {
        camelContext.getRouteController().stopRoute(id);
        camelContext.removeRoute(id);
        if (id.equalsIgnoreCase("runtime_route_config")) {
            DynamicEventBasedRouter dynamicEventBasedRouter = new DynamicEventBasedRouter(applicationEventProcessor);
            camelContext.addRoutes(dynamicEventBasedRouter);
        }
        camelContext.start();
    }
}
