package com.wac.autocore.controller;

import com.wac.autocore.model.Mechanic;
import com.wac.autocore.service.MechanicService;

import java.util.List;
import java.util.Optional;

public class MechanicController {
    private final MechanicService mechanicService;
    private final ErrorHandler errorHandler;

    public MechanicController(MechanicService mechanicService, ErrorHandler errorHandler) {
        this.mechanicService = mechanicService;
        this.errorHandler = errorHandler;
    }

    public Result<List<Mechanic>> getMechanics() {
        return errorHandler.call(mechanicService::getMechanics);
    }

    public Result<List<Mechanic>> getAvailableMechanics() {
        return errorHandler.call(mechanicService::getAvailableMechanics);
    }

    public Result<Optional<Mechanic>> getMechanic(int id) {
        return errorHandler.call(() -> mechanicService.getMechanic(id));
    }

    public Result<Mechanic> updateMechanic(int mechanicId, String name, String phone, String specialization) {
        return errorHandler.call(() -> mechanicService.updateMechanic(mechanicId, name, phone, specialization));
    }

    public Result<Boolean> mechanicHasUncompletedWorkOrders(int mechanicId) {
        return errorHandler.call(() -> mechanicService.mechanicHasUncompletedWorkOrders(mechanicId));
    }

    public Result<Boolean> deleteMechanic(int mechanicId) {
        return errorHandler.call(() -> mechanicService.deleteMechanic(mechanicId));
    }
}