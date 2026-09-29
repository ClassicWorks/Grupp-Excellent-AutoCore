package com.wac.autocore.data;

import com.wac.autocore.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepo {
    Customer save(Customer customer);
    Customer update(Customer customer);
    List<Customer> getAll();
    Optional<Customer> get(int id);
}
