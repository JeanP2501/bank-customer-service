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
                .documentType(request.getDocumentType())
                .documentNumber(request.getDocumentNumber())
                .names(request.getNames())
                .lastName(request.getLastName())
                .motherLastName(request.getMotherLastName())
                .businessName(request.getBusinessName())
                .birthdate(request.getBirthdate())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .address(request.getAddress())
                .hasCreditCard(request.getHasCreditCard() != null
                        ? request.getHasCreditCard()
                        : false)
                .createdAt(LocalDateTime.now())
                .active(true)
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
                .documentType(customer.getDocumentType())
                .documentNumber(customer.getDocumentNumber())
                .names(customer.getNames())
                .lastName(customer.getLastName())
                .motherLastName(customer.getMotherLastName())
                .businessName(customer.getBusinessName())
                .birthdate(customer.getBirthdate())
                .phoneNumber(customer.getPhoneNumber())
                .email(customer.getEmail())
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
        customer.setDocumentNumber(request.getDocumentNumber());
        customer.setNames(request.getNames());
        customer.setLastName(request.getLastName());
        customer.setMotherLastName(request.getMotherLastName());
        customer.setBusinessName(request.getBusinessName());
        customer.setBirthdate(request.getBirthdate());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setUpdatedAt(LocalDateTime.now());
    }
}
