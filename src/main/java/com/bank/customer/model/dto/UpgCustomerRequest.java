package com.bank.customer.model.dto;

import com.bank.customer.model.enums.CustomerType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpgCustomerRequest {

    @NotNull(message = "Customer type is required")
    private CustomerType customerType;

}
