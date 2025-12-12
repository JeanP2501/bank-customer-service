package com.bank.customer.model.dto;

import com.bank.customer.model.enums.CustomerType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for customer creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

    @NotNull(message = "Customer type is required")
    private CustomerType customerType;

    @NotNull(message = "Document type is required")
    private String documentType;

    @NotBlank(message = "Document number is required")
    private String documentNumber;

    private String names;

    private String lastName;

    private String motherLastName;

    private String businessName;

    private LocalDate birthdate;

    @NotNull(message = "Phone number is required")
    private String phoneNumber;

    @Email(message = "Email should be valid")
    private String email;

    private String address;

    /**
     * Indicates if customer has credit card (for VIP/PYME).
     */
    private Boolean hasCreditCard;
}