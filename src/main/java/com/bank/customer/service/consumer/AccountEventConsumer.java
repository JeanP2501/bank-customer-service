package com.bank.customer.service.consumer;

import com.bank.customer.config.KafkaConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountEventConsumer {

    private final KafkaConfig kafkaConfig;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.account-events}")
    private String accountEventsTopic;

    @Value("${kafka.consumer.group-id}")
    private String groupId;

    @PostConstruct
    public void consumeAccountEvents() {
        ReceiverOptions<String, String> options =
                kafkaConfig.receiverOptions(groupId, accountEventsTopic);

        KafkaReceiver.create(options)
                .receive()
                .doOnNext(record -> log.info("Evento recibido de Account: key={}", record.key()))
                .flatMap(record -> {
                    try {
                        // Aquí parsearías el evento específico que necesites
                        log.info("Procesando evento de Account: {}", record.value());

                        return processAccountEvent(record.value())
                                .doOnSuccess(v -> record.receiverOffset().acknowledge())
                                .doOnError(e -> log.error("Error procesando evento: {}", e.getMessage()));
                    } catch (Exception e) {
                        log.error("Error deserializando evento: {}", e.getMessage());
                        record.receiverOffset().acknowledge();
                        return Mono.empty();
                    }
                })
                .subscribe();
    }

    private Mono<Void> processAccountEvent(String eventJson) {
        // Implementa tu lógica de negocio aquí
        log.info("Procesando evento de cuenta");
        return Mono.empty();
    }
}
