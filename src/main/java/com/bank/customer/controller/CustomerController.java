package com.bank.customer.controller;

import com.bank.customer.model.dto.CustomerRequest;
import com.bank.customer.model.dto.CustomerResponse;
import com.bank.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST Controller for Customer operations
 * Provides endpoints for CRUD operations
 */
@Slf4j
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Create a new customer
     * POST /api/customers
     * @param request the customer request
     * @return Mono of CustomerResponse with 201 status
     */
    @PostMapping
    public Mono<ResponseEntity<CustomerResponse>> create(@Valid @RequestBody CustomerRequest request) {
        log.info("POST /api/customers - Creating customer");
        return customerService.create(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    /**
     * Get all customers
     * GET /api/customers
     * @return Flux of CustomerResponse with 200 status
     */
    @GetMapping
    public Mono<ResponseEntity<Flux<CustomerResponse>>> findAll() {
        log.info("GET /api/customers - Fetching all customers");
        return Mono.just(ResponseEntity.ok(customerService.findAll()));
    }

    /**
     * Get customer by ID
     * GET /api/customers/{id}
     * @param id the customer id
     * @return Mono of CustomerResponse with 200 status
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<CustomerResponse>> findById(@PathVariable String id) {
        log.info("GET /api/customers/{} - Fetching customer by id", id);
        return customerService.findById(id)
                .map(ResponseEntity::ok);
    }

    /**
     * Get customer by document number
     * GET /api/customers/document/{documentNumber}
     * @param documentNumber the document number
     * @return Mono of CustomerResponse with 200 status
     */
    @GetMapping("/document/{documentNumber}")
    public Mono<ResponseEntity<CustomerResponse>> findByDocumentNumber(@PathVariable String documentNumber) {
        log.info("GET /api/customers/document/{} - Fetching customer by document number", documentNumber);
        return customerService.findByDocumentNumber(documentNumber)
                .map(ResponseEntity::ok);
    }

    /**
     * Update customer
     * PUT /api/customers/{id}
     * @param id the customer id
     * @param request the customer request
     * @return Mono of CustomerResponse with 200 status
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<CustomerResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody CustomerRequest request) {
        log.info("PUT /api/customers/{} - Updating customer", id);
        return customerService.update(id, request)
                .map(ResponseEntity::ok);
    }

    /**
     * Delete customer
     * DELETE /api/customers/{id}
     * @param id the customer id
     * @return Mono of Void with 204 status
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        log.info("DELETE /api/customers/{} - Deleting customer", id);
        return customerService.delete(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}
