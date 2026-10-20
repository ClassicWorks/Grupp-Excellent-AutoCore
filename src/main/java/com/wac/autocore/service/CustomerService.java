package com.wac.autocore.service;

import com.wac.autocore.data.CustomerRepo;
import com.wac.autocore.data.VehicleRepo;
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
            System.out.println("Customer name cannot be empty.");
            return null;
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
            System.out.println("Customer not found.");
            return false;
        }
        if (customerHasVehicles(customerId)) {
            System.out.println("Customer has vehicles and cannot be deleted.");
            return false;
        }
        customerRepo.delete(customerId);
        return true;
    }
}
