package com.bank.customer.mapper;

import com.bank.customer.model.dto.CustomerRequest;
import com.bank.customer.model.dto.CustomerResponse;
import com.bank.customer.model.entity.Customer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper for converting between Customer entity and DTOs
 */
@Component
public class CustomerMapper {

    /**
     * Convert CustomerRequest to Customer entity
     * @param request the customer request
     * @return Customer entity
     */
    public Customer toEntity(CustomerRequest request) {
        return Customer.builder()
                .customerType(request.getCustomerType())
                .documentNumber(request.getDocumentNumber())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .businessName(request.getBusinessName())
                .taxId(request.getTaxId())
                .address(request.getAddress())
                .hasCreditCard(request.getHasCreditCard() != null
                        ? request.getHasCreditCard()
                        : false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Convert Customer entity to CustomerResponse
     * @param customer the customer entity
     * @return CustomerResponse DTO
     */
    public CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .customerType(customer.getCustomerType())
                .documentNumber(customer.getDocumentNumber())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .businessName(customer.getBusinessName())
                .taxId(customer.getTaxId())
                .address(customer.getAddress())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    /**
     * Update existing Customer entity with request data
     * @param customer the existing customer
     * @param request the update request
     */
    public void updateEntity(Customer customer, CustomerRequest request) {
        customer.setCustomerType(request.getCustomerType());
        customer.setDocumentNumber(request.getDocumentNumber());
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setBusinessName(request.getBusinessName());
        customer.setTaxId(request.getTaxId());
        customer.setAddress(request.getAddress());
        customer.setUpdatedAt(LocalDateTime.now());
    }
}
