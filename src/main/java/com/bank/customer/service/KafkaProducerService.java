package com.bank.customer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaSender<String, String> kafkaSender;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.customer-events}")
    private String customerEventsTopic;

    public <T> Mono<Void> sendEvent(String key, T event) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))
                .flatMap(jsonEvent -> {
                    ProducerRecord<String, String> record =
                            new ProducerRecord<>(customerEventsTopic, key, jsonEvent);

                    return kafkaSender.send(Mono.just(SenderRecord.create(record, UUID.randomUUID())))
                            .doOnNext(result -> log.info("Evento enviado - Topic: {}, Key: {}",
                                    customerEventsTopic, key))
                            .doOnError(error -> log.error("Error enviando evento: {}",
                                    error.getMessage()))
                            .then();
                })
                .onErrorResume(JsonProcessingException.class, e -> {
                    log.error("Error serializando evento: {}", e.getMessage());
                    return Mono.error(e);
                });
    }
}
