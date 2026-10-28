package com.wac.autocore.controller;

import com.wac.autocore.model.Customer;
import com.wac.autocore.service.CustomerService;
import com.wac.autocore.service.ServiceItemService;

import java.util.List;
import java.util.Optional;

public class CustomerController {
    private final CustomerService customerService;
    private final ErrorHandler errorHandler;

    public CustomerController(CustomerService customerService, ErrorHandler errorHandler) {
        this.customerService = customerService;
        this.errorHandler = errorHandler;
    }

    public Result<List<Customer>> getCustomers() {
        return errorHandler.call(customerService::getCustomers);
    }

    public Result<Optional<Customer>> getCustomer(int id) {
        return errorHandler.call(() -> customerService.getCustomer(id));
    }

    public Result<List<Customer>> getCustomerMatching(String query) {
        return errorHandler.call(() -> customerService.getCustomerMatching(query));
    }

    public Result<Customer> createCustomer(
            String name,
            String phone,
            String email
    ) {
        return errorHandler.call(
                () -> customerService.createCustomer(name, phone, email)
        );
    }

    public Result<Customer> updateCustomer(
            int customerId,
            String name,
            String phone,
            String email
    ) {
        return errorHandler.call(
                () -> customerService.updateCustomer(
                        customerId, name, phone, email
                )
        );
    }

    public Result<Boolean> customerHasVehicles(int customerId) {
        return errorHandler.call(
                () -> customerService.customerHasVehicles(customerId)
        );
    }

    public Result<Boolean> deleteCustomer(int customerId) {
        return errorHandler.call(
                () -> customerService.deleteCustomer(customerId)
        );
    }
}
