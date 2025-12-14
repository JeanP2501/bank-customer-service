package com.bank.customer.validator;

import com.bank.customer.exception.BusinessRuleException;
import com.bank.customer.exception.CustomerAlreadyExistsException;
import com.bank.customer.model.dto.CustomerRequest;
import com.bank.customer.model.entity.Customer;
import com.bank.customer.model.enums.CustomerType;
import com.bank.customer.model.enums.DocumentType;
import com.bank.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;

/**
 * Validator for customer business rules.
 * Validates customer creation and updates according to business requirements
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerValidator {

    private final CustomerRepository customerRepository;

    /**
     * Validate customer before creation.
     * @param customer the customer to validate
     * @throws BusinessRuleException if validation fails
     */
    public void validateCustomerCreation(final Customer customer) {
        log.debug("Validating customer creation for type: {}",
                customer.getCustomerType());

        CustomerType type = customer.getCustomerType();

        // VIP and PYME require credit card
        if (type.requiresCreditCard() && !customer.canOpenPremiumAccounts()) {
            String message = String.format(
                    "Customer type %s requires an active credit card", type);
            log.error(message);
            throw new BusinessRuleException(message);
        }
    }

    /**
     * Validate customer before update.
     * @param customer the customer to validate
     * @throws BusinessRuleException if validation fails
     */
    public void validateCustomerUpdate(final Customer customer) {
        log.debug("Validating customer update for type: {}",
                customer.getCustomerType());

        validateCustomerCreation(customer);
    }

    public Mono<Customer> validateUpgrade(final Customer customer, CustomerType newCustomerType){
        if (newCustomerType.requiresCreditCard() && !customer.canOpenPremiumAccounts()) {
            String message = String.format(
                    "Customer type %s requires an active credit card", newCustomerType);
            log.error(message);
            return Mono.error(new BusinessRuleException(message));
        }
        return Mono.just(customer);
    }

    public Mono<CustomerRequest> validateTypDocument(CustomerRequest request) {
        if (!DocumentType.isValid(request.getDocumentType())) {
            String tiposValidos = String.join(", ",
                    Arrays.stream(DocumentType.values())
                            .map(DocumentType::getCode)
                            .toArray(String[]::new)
            );

            return Mono.error(new BusinessRuleException(
                    "Tipo de documento inválido: " + request.getDocumentType() +
                            ". Tipos válidos: " + tiposValidos));
        }
        return Mono.just(request);
    }

    public Mono<CustomerRequest> validateLengthDocument(CustomerRequest request) {
        DocumentType docType = DocumentType.fromCode(request.getDocumentType())
                .orElseThrow(); // Ya validado en el paso anterior

        if (!docType.isValidLength(request.getDocumentNumber())) {
            return Mono.error(new BusinessRuleException(
                    String.format("El %s debe tener exactamente %d dígitos. Recibido: %d",
                            docType.getCode(),
                            docType.getLength(),
                            request.getDocumentNumber().length())));
        }

        return Mono.just(request);
    }

    public Mono<CustomerRequest> validateFormatDocument(CustomerRequest request) {
        DocumentType docType = DocumentType.fromCode(request.getDocumentType())
                .orElseThrow();

        if (!docType.isValidFormat(request.getDocumentNumber())) {
            String mensaje = (docType == DocumentType.DNI || docType == DocumentType.RUC)
                    ? "El " + docType.getCode() + " debe contener solo números"
                    : "El " + docType.getCode() + " debe contener solo letras y números";

            return Mono.error(new BusinessRuleException(mensaje));
        }

        return Mono.just(request);
    }

    public Mono<CustomerRequest> validateUpdate(Customer existingCustomer, CustomerRequest request) {
        return Mono.just(request)
                // Validar tipo de documento
                .flatMap(this::validateTypDocument)
                .flatMap(this::validateLengthDocument)
                .flatMap(this::validateFormatDocument)
                // Validar que no exista otro customer con ese documento
                .flatMap(validRequest -> this.validateUniqueDocument(existingCustomer, validRequest));
    }

    public Mono<CustomerRequest> validateUniqueDocument(Customer existingCustomer, CustomerRequest request) {
        // If the document number has not changed, do not validate.
        if (existingCustomer.getDocumentNumber().equals(request.getDocumentNumber())) {
            return Mono.just(request);
        }

        // If it changed, check that it doesn't exist
        return customerRepository.existsByDocumentNumber(request.getDocumentNumber())
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("Document number already exists: {}", request.getDocumentNumber());
                        return Mono.error(new CustomerAlreadyExistsException(request.getDocumentNumber()));
                    }
                    return Mono.just(request);
                });
    }

}