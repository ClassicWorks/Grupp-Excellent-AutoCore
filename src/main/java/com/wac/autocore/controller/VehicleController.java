package com.wac.autocore.controller;

import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.VehicleService;

import java.util.List;
import java.util.Optional;

public class VehicleController {
    private final VehicleService vehicleService;
    private final ErrorHandler errorHandler;

    public VehicleController(VehicleService vehicleService, ErrorHandler errorHandler) {
        this.vehicleService = vehicleService;
        this.errorHandler = errorHandler;
    }

    public Result<List<Vehicle>> getVehicles() {
        return errorHandler.call(vehicleService::getVehicles);
    }

    public Result<Optional<Vehicle>> getVehicle(int id) {
        return errorHandler.call(() -> vehicleService.getVehicle(id));
    }

    public Result<List<Vehicle>> getCustomersVehicle(int customerId) {
        return errorHandler.call(() -> vehicleService.getCustomersVehicle(customerId));
    }

    public Result<Vehicle> createVehicle(String registrationNumber, String brand, String model,
                                         int year, int customerId) {
        return errorHandler.call(() ->
                vehicleService.createVehicle(registrationNumber, brand, model, year, customerId));
    }

    public Result<Vehicle> updateVehicle(int vehicleId, String registrationNumber, String brand,
                                         String model, int year, int customerId) {
        return errorHandler.call(() ->
                vehicleService.updateVehicle(vehicleId, registrationNumber, brand, model, year, customerId));
    }

    public Result<Boolean> vehicleHasBookings(int vehicleId) {
        return errorHandler.call(() -> vehicleService.vehicleHasBookings(vehicleId));
    }

    public Result<Boolean> deleteVehicle(int vehicleId) {
        return errorHandler.call(() -> vehicleService.deleteVehicle(vehicleId));
    }
}