package com.bank.customer.model.dto.events;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseEvent {
    private String eventId;
    private String eventType; // CUSTOMER_CREATED
    private LocalDateTime timestamp;
}
