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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerRequest {

    @NotNull(message = "Customer type is required")
    private CustomerType customerType;

    @NotNull(message = "Document type is required")
    private String documentType;

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

}
