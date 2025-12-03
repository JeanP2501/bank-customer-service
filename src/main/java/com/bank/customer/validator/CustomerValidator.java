package com.bank.customer.validator;

import com.bank.customer.exception.BusinessRuleException;
import com.bank.customer.model.entity.Customer;
import com.bank.customer.model.enums.CustomerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validator for customer business rules.
 * Validates customer creation and updates according to business requirements
 */
@Slf4j
@Component
public class CustomerValidator {

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
}