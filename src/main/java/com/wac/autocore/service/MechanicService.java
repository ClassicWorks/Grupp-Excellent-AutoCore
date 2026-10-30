package com.wac.autocore.service;

import com.wac.autocore.data.MechanicRepo;
import com.wac.autocore.data.WorkOrderRepo;
import com.wac.autocore.exceptions.DeletionNotAllowedException;
import com.wac.autocore.exceptions.InputNotValidException;
import com.wac.autocore.exceptions.MechanicNotFoundException;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.security.AccessGuard;
import com.wac.autocore.security.Permission;

import java.util.List;
import java.util.Optional;

public class MechanicService {
    private final MechanicRepo mechanicRepo;
    private final WorkOrderRepo workOrderRepo;
    private final AccessGuard accessGuard;

    public MechanicService(MechanicRepo mechanicRepo, WorkOrderRepo workOrderRepo, AccessGuard accessGuard) {
        this.mechanicRepo = mechanicRepo;
        this.workOrderRepo = workOrderRepo;
        this.accessGuard = accessGuard;
    }

    public List<Mechanic> getMechanics() {
        accessGuard.require(Permission.READ, "getMechanics");
        List<Mechanic> mechanics = mechanicRepo.getAll();
        if (mechanics.isEmpty()) {
            System.out.println("No mechanics found.");
        }

        return mechanics;
    }

    public List<Mechanic> getAvailableMechanics() {
        accessGuard.require(Permission.READ, "getAvailableMechanics");
        List<Mechanic> mechanics = mechanicRepo.getAllAvailable();
        if (mechanics.isEmpty()) {
            System.out.println("No available mechanics found.");
        }

        return mechanics;
    }

    public Optional<Mechanic> getMechanic(int id){
        accessGuard.require(Permission.READ, "getMechanic");
        return mechanicRepo.get(id);
    }

    /**
     * Updates the details of an existing mechanic.
     *
     * @param mechanicId the unique identifier of the mechanic to update
     * @param name the updated name of the mechanic; must not be null or empty
     * @param phone the updated phone number of the mechanic; may be null
     * @param specialization the updated specialization of the mechanic; may be null
     * @return the updated Mechanic instance with the changes applied
     * @throws InputNotValidException if the provided name is null or empty
     * @throws MechanicNotFoundException if no mechanic is found with the given mechanicId
     */
    public Mechanic updateMechanic(int mechanicId, String name, String phone, String specialization) {
        accessGuard.require(Permission.MANAGE_MECHANICS, "updateMechanic");

        if (name == null || name.trim().isEmpty() || name.trim().length() > 50) {
            throw new InputNotValidException("Mechanic name cannot be empty or longer than 50 characters.");
        }
        if(phone != null && phone.trim().length() > 20)
            throw new InputNotValidException("Phone cannot be longer than 20 characters");

        if(specialization != null && specialization.trim().length() > 50)
            throw new InputNotValidException("Specialization cannot be longer than 50 characters");

        Mechanic mechanic = getMechanic(mechanicId)
                .orElseThrow(() -> new MechanicNotFoundException(mechanicId));

        mechanic.setName(name.trim());
        mechanic.setPhone(phone == null ? null : phone.trim());
        mechanic.setSpecialization(specialization == null ? null : specialization.trim());

        return mechanicRepo.update(mechanic);
    }

    public boolean mechanicHasUncompletedWorkOrders(int mechanicId) {
        accessGuard.require(Permission.READ, "mechanicHasUncompletedWorkOrders");
        return workOrderRepo.mechanicHasUncompletedWorkOrders(mechanicId);
    }

    /**
     * Deletes a mechanic with the specified ID from the system.
     * The method ensures that the mechanic exists and is not assigned to any uncompleted work orders before deletion.
     * All work orders previously assigned to the mechanic are updated to disconnect the mechanic.
     *
     * @param mechanicId the unique identifier of the mechanic to be deleted
     * @return true if the mechanic is successfully deleted
     * @throws MechanicNotFoundException if no mechanic is found with the specified ID
     * @throws DeletionNotAllowedException if the mechanic has uncompleted work orders and cannot be deleted
     */
    //TODO implementera UnitOfWork då när man tar bort en mekaniker ska den koppla bort den från existerande
    // workOrders (som ej är klar/avbrutna)
    public boolean deleteMechanic(int mechanicId) {
        accessGuard.require(Permission.MANAGE_MECHANICS, "deleteMechanic");

        if (mechanicRepo.get(mechanicId).isEmpty()) {
            throw new MechanicNotFoundException(mechanicId);
        }
        //If mechanic has work assigned they cannot be deleted. Connected WorkOrders need to be completed,
        // canceled or deleted
        if (mechanicHasUncompletedWorkOrders(mechanicId)) {
            throw new DeletionNotAllowedException("Mechanic has work orders and cannot be deleted.");
        }
        //All workOrders connected gets disconnected -> mechanic is gone but not past workOrders
        List<WorkOrder> mechanicsWorkOrders = workOrderRepo.getWorkOrdersByMechanic(mechanicId);
        for(WorkOrder workOrder : mechanicsWorkOrders){
            workOrder.setMechanic(null);
            workOrderRepo.update(workOrder);
        }

        mechanicRepo.delete(mechanicId);
        return true;
    }
}