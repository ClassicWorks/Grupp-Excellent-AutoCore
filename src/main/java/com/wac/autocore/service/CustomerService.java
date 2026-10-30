package com.wac.autocore.service;

import com.wac.autocore.data.CustomerRepo;
import com.wac.autocore.data.VehicleRepo;
import com.wac.autocore.exceptions.CustomerNotFoundException;
import com.wac.autocore.exceptions.DeletionNotAllowedException;
import com.wac.autocore.exceptions.InputNotValidException;
import com.wac.autocore.model.Customer;
import com.wac.autocore.security.AccessGuard;
import com.wac.autocore.security.Permission;

import java.util.List;
import java.util.Optional;

public class CustomerService {
    private final CustomerRepo customerRepo;
    private final VehicleRepo vehicleRepo;
    private final AccessGuard accessGuard;

    public CustomerService(CustomerRepo customerRepo, VehicleRepo vehicleRepo, AccessGuard accessGuard) {
        this.customerRepo = customerRepo;
        this.vehicleRepo = vehicleRepo;
        this.accessGuard = accessGuard;
    }

    public List<Customer> getCustomers() {
        accessGuard.require(Permission.READ, "getCustomers");
        if (customerRepo.getAll().isEmpty()) {
            System.out.println("No customers found.");
        }
        return customerRepo.getAll();
    }

    public Optional<Customer> getCustomer(int id){
        accessGuard.require(Permission.READ, "getCustomer");
        return customerRepo.get(id);
    }

    public List<Customer> getCustomersMatching(String query) {
        accessGuard.require(Permission.READ, "getCustomersMatching");
        return customerRepo.getMatchingName(query);
    }

    /**
     * Creates a new customer with the specified details.
     * User must have permission to MANAGE_CUSTOMERS
     *
     * @param name the name of the customer; must not be null or empty.
     * @param phone the phone number of the customer; can be null but at least one of phone or email must be provided.
     * @param email the email address of the customer; can be null but must contain "@" if provided.
     * @return the saved Customer instance.
     * @throws InputNotValidException if the name is null or empty, if both phone and email are empty,
     *                                or if the email format is invalid.
     */
    public Customer createCustomer(String name, String phone, String email) {
        accessGuard.require(Permission.MANAGE_CUSTOMERS, "createCustomer");

        if(name == null || name.trim().isEmpty() || name.trim().length() > 50)
            throw new InputNotValidException("Customer name cannot be empty or greater than 50 character.");

        if((phone == null || phone.trim().isEmpty()) && (email == null || email.trim().isEmpty()))
            throw new InputNotValidException("Email or phone cannot both be empty.");

        if(phone != null && phone.trim().length() > 20)
            throw new InputNotValidException("Phone cannot be greater than 20 character.");

        if(email != null) {
            if (email.trim().length() > 50)
                throw new InputNotValidException("Email cannot be longer than 20 characters.");

            if (!email.contains("@"))
                throw new InputNotValidException("Wrong format om email");
        }

        Customer customer = new Customer(name.trim(), phone, email);

        Customer savedCustomer = customerRepo.save(customer);

        System.out.println("Customer created successfully.");
        System.out.println(savedCustomer);

        return savedCustomer;
    }

    /**
     * Updates the details of an existing customer with the specified ID.
     * User must have permission to MANAGE_CUSTOMERS
     *
     * @param customerId the ID of the customer to update.
     * @param name the updated name of the customer; must not be null or empty.
     * @param phone the updated phone number of the customer; can be null but at least one of phone or email must be provided.
     * @param email the updated email address of the customer; can be null but must contain "@" if provided.
     * @return the saved Customer instance.
     * @throws InputNotValidException if the name is null or empty, if both phone and email are empty,
     *                                 or if the email format is invalid.
     * @throws CustomerNotFoundException if no customer with the given ID exists.
     */
    public Customer updateCustomer(int customerId, String name, String phone, String email) {
        accessGuard.require(Permission.MANAGE_CUSTOMERS, "updateCustomer");

        if(name == null || name.trim().isEmpty() || name.trim().length() > 50)
            throw new InputNotValidException("Customer name cannot be empty or greater than 50 character.");

        if((phone == null || phone.trim().isEmpty()) && (email == null || email.trim().isEmpty()))
            throw new InputNotValidException("Email or phone cannot both be empty.");

        if(phone != null && phone.trim().length() > 20)
            throw new InputNotValidException("Phone cannot be greater than 20 character.");

        if(email != null && !email.isBlank()) {
            if (email.trim().length() > 50)
                throw new InputNotValidException("Email cannot be longer than 50 characters.");

            if (!email.contains("@"))
                throw new InputNotValidException("Wrong format om email");
        }

        Customer customer = getCustomer(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        customer.setName(name.trim());
        customer.setPhone(phone == null ? null : phone.trim());
        customer.setEmail(email == null ? null : email.trim().toLowerCase());

        return customerRepo.update(customer);
    }

    public boolean customerHasVehicles(int customerId) {
        accessGuard.require(Permission.READ, "customerHasVehicles");

        return vehicleRepo.existsByCustomerId(customerId);
    }

    /**
     * Deletes a customer with the given customer ID. Associated vehicles must be deleted before
     * the customer can be deleted
     * User must have permission to MANAGE_CUSTOMERS
     *
     * @param customerId the ID of the customer to be deleted
     * @return true if the customer is successfully deleted
     * @throws CustomerNotFoundException if no customer with the specified ID exists
     * @throws DeletionNotAllowedException if the customer has associated vehicles
     */
    public boolean deleteCustomer(int customerId) {
        accessGuard.require(Permission.MANAGE_CUSTOMERS, "deleteCustomer");

        if (customerRepo.get(customerId).isEmpty()) {
            throw new CustomerNotFoundException("Customer not found.");
        }
        if (customerHasVehicles(customerId)) {
            throw new DeletionNotAllowedException("Customer has vehicles and cannot be deleted.");
        }
        customerRepo.delete(customerId);
        return true;
    }
}
