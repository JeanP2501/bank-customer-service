package com.bank.customer.model.dto;

import com.bank.customer.model.enums.CustomerType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @NotBlank(message = "Document number is required")
    private String documentNumber;

    private String firstName;

    private String lastName;

    @Email(message = "Email should be valid")
    private String email;

    private String phoneNumber;

    private String businessName;

    private String taxId;

    private String address;

    /**
     * Indicates if customer has credit card (for VIP/PYME).
     */
    private Boolean hasCreditCard;
}