package com.bank.customer.model.dto;

import com.bank.customer.model.enums.CustomerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String documentNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String businessName;
    private String taxId;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
