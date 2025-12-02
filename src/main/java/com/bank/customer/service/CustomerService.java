package com.bank.customer.service;

import com.bank.customer.exception.CustomerAlreadyExistsException;
import com.bank.customer.exception.CustomerNotFoundException;
import com.bank.customer.mapper.CustomerMapper;
import com.bank.customer.model.dto.CustomerRequest;
import com.bank.customer.model.dto.CustomerResponse;
import com.bank.customer.model.entity.Customer;
import com.bank.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service layer for Customer operations
 * Implements business logic for customer management
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    /**
     * Create a new customer
     * @param request the customer request
     * @return Mono of CustomerResponse
     */
    public Mono<CustomerResponse> create(CustomerRequest request) {
        log.debug("Creating customer with document number: {}", request.getDocumentNumber());

        return customerRepository.existsByDocumentNumber(request.getDocumentNumber())
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("Customer already exists with document number: {}", request.getDocumentNumber());
                        return Mono.error(new CustomerAlreadyExistsException(request.getDocumentNumber()));
                    }

                    Customer customer = customerMapper.toEntity(request);
                    return customerRepository.save(customer)
                            .doOnSuccess(c -> log.info("Customer created successfully with id: {}", c.getId()))
                            .map(customerMapper::toResponse);
                });
    }

    /**
     * Find all customers
     * @return Flux of CustomerResponse
     */
    public Flux<CustomerResponse> findAll() {
        log.debug("Finding all customers");
        return customerRepository.findAll()
                .map(customerMapper::toResponse)
                .doOnComplete(() -> log.debug("Retrieved all customers"));
    }

    /**
     * Find customer by ID
     * @param id the customer id
     * @return Mono of CustomerResponse
     */
    public Mono<CustomerResponse> findById(String id) {
        log.debug("Finding customer by id: {}", id);
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(id)))
                .map(customerMapper::toResponse)
                .doOnSuccess(c -> log.debug("Customer found with id: {}", id));
    }

    /**
     * Find customer by document number
     * @param documentNumber the document number
     * @return Mono of CustomerResponse
     */
    public Mono<CustomerResponse> findByDocumentNumber(String documentNumber) {
        log.debug("Finding customer by document number: {}", documentNumber);
        return customerRepository.findByDocumentNumber(documentNumber)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("documentNumber", documentNumber)))
                .map(customerMapper::toResponse)
                .doOnSuccess(c -> log.debug("Customer found with document number: {}", documentNumber));
    }

    /**
     * Update customer
     * @param id the customer id
     * @param request the customer request
     * @return Mono of CustomerResponse
     */
    public Mono<CustomerResponse> update(String id, CustomerRequest request) {
        log.debug("Updating customer with id: {}", id);

        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(id)))
                .flatMap(existingCustomer -> {
                    // Check if document number is being changed and if it already exists
                    if (!existingCustomer.getDocumentNumber().equals(request.getDocumentNumber())) {
                        return customerRepository.existsByDocumentNumber(request.getDocumentNumber())
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new CustomerAlreadyExistsException(request.getDocumentNumber()));
                                    }
                                    customerMapper.updateEntity(existingCustomer, request);
                                    return customerRepository.save(existingCustomer);
                                });
                    }

                    customerMapper.updateEntity(existingCustomer, request);
                    return customerRepository.save(existingCustomer);
                })
                .doOnSuccess(c -> log.info("Customer updated successfully with id: {}", id))
                .map(customerMapper::toResponse);
    }

    /**
     * Delete customer by ID
     * @param id the customer id
     * @return Mono of Void
     */
    public Mono<Void> delete(String id) {
        log.debug("Deleting customer with id: {}", id);

        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(id)))
                .flatMap(customer -> customerRepository.deleteById(id)
                        .doOnSuccess(v -> log.info("Customer deleted successfully with id: {}", id)));
    }
}
