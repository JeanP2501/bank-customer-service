package com.bank.customer.exception;

/**
 * Exception thrown when trying to create a customer that already exists
 */
public class CustomerAlreadyExistsException extends RuntimeException {

    public CustomerAlreadyExistsException(String documentNumber) {
        super("Customer already exists with document number: " + documentNumber);
    }
}
