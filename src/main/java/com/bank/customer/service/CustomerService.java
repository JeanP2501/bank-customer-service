package com.bank.customer.service;

import com.bank.customer.exception.CustomerAlreadyExistsException;
import com.bank.customer.exception.CustomerNotFoundException;
import com.bank.customer.mapper.CustomerMapper;
import com.bank.customer.model.dto.CustomerRequest;
import com.bank.customer.model.dto.CustomerResponse;
import com.bank.customer.model.dto.UpgCustomerRequest;
import com.bank.customer.model.dto.events.EntityActionEvent;
import com.bank.customer.model.entity.Customer;
import com.bank.customer.repository.CustomerRepository;
import com.bank.customer.validator.CustomerValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

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
    private final CustomerValidator customerValidator;
    private final KafkaProducerService kafkaProducerService;

    /**
     * Create a new customer
     * @param request the customer request
     * @return Mono of CustomerResponse
     */
    public Mono<CustomerResponse> create(CustomerRequest request) {
        log.debug("Creating customer with document number: {}", request.getDocumentNumber());

        return Mono.just(request)
                .flatMap(customerValidator::validateTypDocument)
                .flatMap(customerValidator::validateLengthDocument)
                .flatMap(customerValidator::validateFormatDocument)
                .flatMap(req -> customerRepository.existsByDocumentNumber(req.getDocumentNumber())
                        .flatMap(exists -> {
                            if (exists) {
                                log.warn("Customer already exists with document number: {}", req.getDocumentNumber());
                                return Mono.error(new CustomerAlreadyExistsException(req.getDocumentNumber()));
                            }

                            Customer customer = customerMapper.toEntity(req);
                            customerValidator.validateCustomerCreation(customer);

                            return customerRepository.save(customer)
                                    .flatMap(savedCustomer -> {
                                        // Publicar evento después de guardar exitosamente
                                        EntityActionEvent event = EntityActionEvent.builder()
                                                .eventId(UUID.randomUUID().toString())
                                                .eventType("CUSTOMER_CREATED")
                                                .entityType(savedCustomer.getClass().getSimpleName())
                                                .payload(savedCustomer)
                                                .timestamp(LocalDateTime.now())
                                                .build();

                                        return kafkaProducerService.sendEvent(savedCustomer.getId(), event)
                                                .doOnSuccess(v -> log.info("Customer created and event published: {}", savedCustomer.getId()))
                                                .doOnError(e -> log.error("Error publishing event: {}", e.getMessage()))
                                                .thenReturn(savedCustomer);
                                    })
                                    .map(customerMapper::toResponse);
                        })
                );
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
                .flatMap(existingCustomer ->
                        customerValidator.validateUpdate(existingCustomer, request)
                            .flatMap(validRequest ->
                                    updateCustomer(existingCustomer, validRequest))
                )
                .flatMap(this::publicEventUpdateKafka)
                .map(customerMapper::toResponse);
    }

    private Mono<Customer> updateCustomer(Customer existingCustomer, CustomerRequest request) {
        customerValidator.validateCustomerCreation(existingCustomer);
        customerMapper.updateEntity(existingCustomer, request);
        return customerRepository.save(existingCustomer);
    }

    private Mono<Customer> publicEventUpdateKafka(Customer updatedCustomer) {
        EntityActionEvent event = EntityActionEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("CUSTOMER_UPDATED")
                .entityType(updatedCustomer.getClass().getSimpleName())
                .timestamp(LocalDateTime.now())
                .payload(updatedCustomer)
                .build();

        return kafkaProducerService.sendEvent(updatedCustomer.getId(), event)
                .doOnSuccess(v -> log.info("Customer updated and event published: {}", updatedCustomer.getId()))
                .doOnError(e -> log.error("Error publishing event: {}", e.getMessage()))
                .thenReturn(updatedCustomer);
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
                .flatMap(customer -> {
                    customer.setActive(false);
                    return customerRepository.save(customer)
                            .flatMap(savedCustomer -> {
                                // Publicar evento después de eliminar exitosamente
                                EntityActionEvent event = EntityActionEvent.builder()
                                        .eventId(UUID.randomUUID().toString())
                                        .eventType("CUSTOMER_DELETED")
                                        .entityType(savedCustomer.getClass().getSimpleName())
                                        .timestamp(LocalDateTime.now())
                                        .payload(savedCustomer)
                                        .build();

                                return kafkaProducerService.sendEvent(id, event)
                                        .doOnSuccess(v -> log.info("Customer deleted and event published: {}", id))
                                        .doOnError(e -> log.error("Error publishing event: {}", e.getMessage()));
                            });
                })
                .then();
    }



    public Mono<CustomerResponse> upgrade(String id, UpgCustomerRequest request) {
        log.debug("Upgrade type customer with id: {}", id);
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(id)))
                .flatMap(customer -> customerValidator.validateUpgrade(customer, request.getCustomerType()))
                .doOnNext(customer -> customer.setCustomerType(request.getCustomerType()))
                .flatMap(customerRepository::save)
                .map(customerMapper::toResponse);
    }

}
