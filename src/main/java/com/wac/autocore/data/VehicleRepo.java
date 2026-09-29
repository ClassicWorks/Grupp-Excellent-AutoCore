package com.wac.autocore.data;

import com.wac.autocore.model.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleRepo {
    Vehicle saveVehicle(Vehicle v);
    List<Vehicle> getVehicles();
    Optional<Vehicle> getVehicle(int id);
}
