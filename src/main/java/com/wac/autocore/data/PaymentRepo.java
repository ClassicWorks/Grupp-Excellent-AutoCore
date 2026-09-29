package com.wac.autocore.data;

import com.wac.autocore.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepo {
    Payment save(Payment p);
    List<Payment> getAll();
    Optional<Payment> get(int id);
}
