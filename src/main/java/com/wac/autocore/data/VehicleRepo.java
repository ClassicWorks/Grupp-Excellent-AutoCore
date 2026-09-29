package com.wac.autocore.data;

import com.wac.autocore.model.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleRepo {
    Vehicle save(Vehicle v);
    Vehicle update(Vehicle v);
    List<Vehicle> getAll();
    Optional<Vehicle> get(int id);
}
