package com.camel.integration.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("idGeneratorService")
public class IDGeneratorService {

    public String getId() {
        return "Id: " + UUID.randomUUID();
    }
}
