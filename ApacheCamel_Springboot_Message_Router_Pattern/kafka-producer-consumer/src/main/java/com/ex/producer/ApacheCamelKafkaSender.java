package com.ex.producer;

import com.ex.model.BookingRequest;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
public class ApacheCamelKafkaSender {

    @Autowired
    @Qualifier("camelKafkaTemplate")
    private KafkaTemplate<String, BookingRequest> bookingTemplate;

    @Value("${app.kafka.camel-topic}")
    private String topicName;

    public String send(BookingRequest bookingRequest) {
        log.info("sending data='{}={}' to topic='{}'", bookingRequest.hashCode(), bookingRequest, topicName);
        ProducerRecord<String, BookingRequest> producerRecord = new ProducerRecord<>(topicName, UUID.randomUUID().toString(), bookingRequest);
        producerRecord.headers().add("X-ORDER-ID", UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        producerRecord.headers().add("X-TASK-NAME", getHttpHeader("X-TASK-NAME").getBytes(StandardCharsets.UTF_8));
        ListenableFuture<SendResult<String, BookingRequest>> response = bookingTemplate.send(producerRecord);
        logKafkaResponse(producerRecord, response);
        return "data sent successfully";
    }

    @SneakyThrows
    private <T> void logKafkaResponse(ProducerRecord<String, T> producerRecord, ListenableFuture<SendResult<String, T>> future) {
        future.addCallback(new ListenableFutureCallback<SendResult<String, T>>() {
            @Override
            public void onSuccess(SendResult<String, T> result) {

            }

            @Override
            public void onFailure(@NonNull Throwable ex) {
                log.error("Unable to send Payload to kafka topic:[{}] due to : {}", producerRecord.topic(), ex);
            }
        });
    }

    public static String getHttpHeader(String headerName) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects
                .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String headerData = request.getHeader(headerName);
        return Objects.isNull(headerData) ? "BOOKING" : headerData;
    }

}
