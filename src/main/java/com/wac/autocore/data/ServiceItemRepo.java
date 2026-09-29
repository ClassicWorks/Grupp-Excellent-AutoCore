package com.wac.autocore.data;

import com.wac.autocore.model.ServiceItem;

import java.util.List;
import java.util.Optional;

public interface ServiceItemRepo {
    ServiceItem save(ServiceItem si);
    List<ServiceItem> getAll();
    Optional<ServiceItem> get(int id);
}
