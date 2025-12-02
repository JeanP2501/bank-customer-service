package com.bank.customer.exception;

/**
 * Exception thrown when a customer is not found
 */
public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String id) {
        super("Customer not found with id: " + id);
    }

    public CustomerNotFoundException(String field, String value) {
        super(String.format("Customer not found with %s: %s", field, value));
    }
}
