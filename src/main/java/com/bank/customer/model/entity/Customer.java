package com.bank.customer.model.entity;

import com.bank.customer.model.enums.CustomerType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Customer entity representing bank customers
 * Supports both personal and business customer types
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customers")
public class Customer {

    @Id
    private String id;

    @NotNull(message = "Customer type is required")
    private CustomerType customerType;

    @NotNull(message = "Document type is required")
    private String documentType;

    // For PERSONAL customers
    @NotBlank(message = "Document number is required")
    private String documentNumber;

    @NotBlank(message = "Names is required")
    private String names;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Mother last name is required")
    private String motherLastName;

    private String businessName;

    private LocalDate birthdate;

    @NotNull(message = "Phone number is required")
    private String phoneNumber;

    @Email(message = "Email should be valid")
    private String email;

    private String address;

    /**
     * Indicates if customer has an active credit card.
     */
    private Boolean hasCreditCard = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    private boolean active = false;

    /**
     * Check if customer can open VIP or PYME accounts.
     * @return true if has credit card
     */
    public boolean canOpenPremiumAccounts() {
        return Boolean.TRUE.equals(hasCreditCard);
    }

}