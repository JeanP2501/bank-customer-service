package com.bank.customer.model.dto;

import com.bank.customer.model.enums.CustomerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for customer response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private String id;
    private CustomerType customerType;
    private String documentType;
    private String documentNumber;
    private String names;
    private String lastName;
    private String motherLastName;
    private String businessName;
    private LocalDate birthdate;
    private String phoneNumber;
    private String email;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /**
     * Indicates if customer has credit card.
     */
    private Boolean hasCreditCard;
}
