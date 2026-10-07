package com.wac.autocore.data;

import com.wac.autocore.model.ServicePack;

import java.util.List;
import java.util.Optional;

public interface ServicePackRepo {
    ServicePack save(ServicePack sp);
    ServicePack update(ServicePack sp);
    List<ServicePack> getAll();
    Optional<ServicePack> getById(String id);
    boolean nameAvailable(String name);
    void delete(String id);

}
