package com.wac.autocore.service;

import com.wac.autocore.data.CustomerRepo;
import com.wac.autocore.data.VehicleRepo;
import com.wac.autocore.exceptions.CustomerNotFoundException;
import com.wac.autocore.exceptions.DeletionNotAllowedException;
import com.wac.autocore.exceptions.InputNotValidException;
import com.wac.autocore.model.Customer;

import java.util.List;
import java.util.Optional;

public class CustomerService {
    private final CustomerRepo customerRepo;
    private final VehicleRepo vehicleRepo;

    public CustomerService(CustomerRepo customerRepo, VehicleRepo vehicleRepo) {
        this.customerRepo = customerRepo;
        this.vehicleRepo = vehicleRepo;
    }

    public List<Customer> getCustomers() {
        if (customerRepo.getAll().isEmpty()) {
            System.out.println("No customers found.");
        }
        return customerRepo.getAll();
    }

    public Optional<Customer> getCustomer(int id){
        return customerRepo.get(id);
    }

    public List<Customer> getCustomerMatching(String query) {
        return customerRepo.getMatchingName(query);
    }

    public Customer createCustomer(String name, String phone, String email) {
        //TODO check if values are correct and normalize, name not too long, email contains correct
        Customer customer = new Customer(name, phone, email);

        Customer savedCustomer = customerRepo.save(customer);

        System.out.println("Customer created successfully.");
        System.out.println(savedCustomer);

        return savedCustomer;
    }

    public Customer updateCustomer(int customerId, String name, String phone, String email) {
        if (name == null || name.trim().isEmpty()) {
            throw new InputNotValidException("Customer name cannot be empty.");
        }

        Optional<Customer> optionalCustomer = getCustomer(customerId);

        if (!optionalCustomer.isPresent()) {
            System.out.println("Customer with ID " + customerId + " does not exist.");
            return null;
        }

        Customer customer = optionalCustomer.get();
        customer.setName(name.trim());
        customer.setPhone(phone);
        customer.setEmail(email);

        return customerRepo.update(customer);
    }

    public boolean customerHasVehicles(int customerId) {
        return vehicleRepo.customerHasVehicle(customerId);
    }

    public boolean deleteCustomer(int customerId) {
        if (!customerRepo.get(customerId).isPresent()) {
            throw new CustomerNotFoundException("Customer not found.");
        }
        if (customerHasVehicles(customerId)) {
            throw new DeletionNotAllowedException("Customer has vehicles and cannot be deleted.");
        }
        customerRepo.delete(customerId);
        return true;
    }

    public void showCustomers() {
        System.out.println();
        System.out.println("=== CUSTOMERS ===");

        if (customerRepo.getAll().isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        for (Customer customer : customerRepo.getAll()) {
            System.out.println(customer);
        }
    }
}
