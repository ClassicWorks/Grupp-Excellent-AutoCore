package com.wac.autocore.data;

import com.wac.autocore.model.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepo {
    Invoice save(Invoice i);
    Invoice update(Invoice i);
    List<Invoice> getAll();
    List<Invoice> findByPayed(boolean isPayed);
    boolean existsByWorkOrderId(int workOrderId);
    Optional<Invoice> get(int id);
}
