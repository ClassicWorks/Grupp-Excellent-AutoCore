package com.wac.autocore.service;

import com.wac.autocore.data.MechanicRepo;
import com.wac.autocore.data.WorkOrderRepo;
import com.wac.autocore.exceptions.DeletionNotAllowedException;
import com.wac.autocore.exceptions.InputNotValidException;
import com.wac.autocore.exceptions.MechanicNotFoundException;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.WorkOrder;

import java.util.List;
import java.util.Optional;

public class MechanicService {
    private final MechanicRepo mechanicRepo;
    private final WorkOrderRepo workOrderRepo;

    public MechanicService(MechanicRepo mechanicRepo, WorkOrderRepo workOrderRepo) {
        this.mechanicRepo = mechanicRepo;
        this.workOrderRepo = workOrderRepo;
    }

    public void showMechanics() {
        System.out.println();
        System.out.println("=== MECHANICS ===");

        if (mechanicRepo.getAll().isEmpty()) {
            System.out.println("No mechanics found.");
            return;
        }

        for (Mechanic mechanic : mechanicRepo.getAll()) {
            System.out.println(mechanic);
        }
    }

    public List<Mechanic> getMechanics() {
        List<Mechanic> mechanics = mechanicRepo.getAll();
        if (mechanics.isEmpty()) {
            System.out.println("No mechanics found.");
        }

        return mechanics;
    }

    public List<Mechanic> getAvailableMechanics() {
        List<Mechanic> mechanics = mechanicRepo.getAllAvailable();
        if (mechanics.isEmpty()) {
            System.out.println("No available mechanics found.");
        }

        return mechanics;
    }

    public Optional<Mechanic> getMechanic(int id){
        return mechanicRepo.get(id);
    }

    public Mechanic updateMechanic(int mechanicId, String name, String phone, String specialization) {
        if (name == null || name.trim().isEmpty()) {
            throw new InputNotValidException("Mechanic name cannot be empty.");
        }

        Optional<Mechanic> optionalMechanic = getMechanic(mechanicId);

        if (!optionalMechanic.isPresent()) {
            throw new MechanicNotFoundException(mechanicId);
        }

        Mechanic mechanic = optionalMechanic.get();
        mechanic.setName(name.trim());
        mechanic.setPhone(phone);
        mechanic.setSpecialization(specialization);

        return mechanicRepo.update(mechanic);
    }

    public boolean mechanicHasUncompletedWorkOrders(int mechanicId) {
        return workOrderRepo.mechanicHasUncompletedWorkOrders(mechanicId);
    }

    //TODO implementera UnitOfWork då när man tar bort en mekaniker ska den koppla bort den från existerande
    // workOrders (som ej är klar/avbrutna)
    public boolean deleteMechanic(int mechanicId) {
        if (!mechanicRepo.get(mechanicId).isPresent()) {
            throw new MechanicNotFoundException(mechanicId);
        }
        //If mechanic has work assigned they cannot be deleted. Connected WorkOrders need to be completed,
        // canceled or deleted
        if (mechanicHasUncompletedWorkOrders(mechanicId)) {
            throw new DeletionNotAllowedException("Mechanic has work orders and cannot be deleted.");
        }
        //All workOrders connected gets disconnected
        List<WorkOrder> mechanicsWorkOrders = workOrderRepo.getWorkOrdersByMechanic(mechanicId);
        for(WorkOrder workOrder : mechanicsWorkOrders){
            workOrder.setMechanic(null);
            workOrderRepo.update(workOrder);
        }

        mechanicRepo.delete(mechanicId);
        return true;
    }
}
