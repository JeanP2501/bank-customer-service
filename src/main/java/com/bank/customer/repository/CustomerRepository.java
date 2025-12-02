package com.bank.customer.repository;

import com.bank.customer.model.entity.Customer;
import com.bank.customer.model.enums.CustomerType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive repository for Customer entity
 * Provides CRUD operations and custom queries
 */
@Repository
public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {

    /**
     * Find customer by document number
     * @param documentNumber the document number
     * @return Mono of Customer
     */
    Mono<Customer> findByDocumentNumber(String documentNumber);

    /**
     * Find customers by customer type
     * @param customerType the customer type
     * @return Flux of Customers
     */
    Flux<Customer> findByCustomerType(CustomerType customerType);

    /**
     * Check if customer exists by document number
     * @param documentNumber the document number
     * @return Mono of Boolean
     */
    Mono<Boolean> existsByDocumentNumber(String documentNumber);
}