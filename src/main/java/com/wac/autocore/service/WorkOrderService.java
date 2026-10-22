package com.wac.autocore.service;

import com.wac.autocore.data.*;
import com.wac.autocore.exceptions.*;
import com.wac.autocore.model.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WorkOrderService {
    private final BookingRepo bookingRepo;
    private final WorkOrderRepo workOrderRepo;
    private final MechanicRepo mechanicRepo;
    private final ServiceItemRepo serviceItemRepo;

    public WorkOrderService(BookingRepo bookingRepo, WorkOrderRepo workOrderRepo, MechanicRepo mechanicRepo, ServiceItemRepo serviceItemRepo) {
        this.bookingRepo = bookingRepo;
        this.workOrderRepo = workOrderRepo;
        this.mechanicRepo = mechanicRepo;
        this.serviceItemRepo = serviceItemRepo;
    }

    public void showWorkOrders() {
        System.out.println();
        System.out.println("=== WORK ORDERS ===");

        if (workOrderRepo.getAll().isEmpty()) {
            System.out.println("No work orders found.");
            return;
        }

        for (WorkOrder workOrder : workOrderRepo.getAll()) {
            System.out.println(workOrder);
        }
    }

    public List<WorkOrder> getWorkOrders() {
        List<WorkOrder> workOrders = workOrderRepo.getAll();
        if (workOrders.isEmpty()) {
            System.out.println("No work orders found.");
        }

        return workOrders;
    }

    public List<WorkOrder> getWorkOrdersWithItems(){
        List<WorkOrder> workOrders = workOrderRepo.getAllWithItems();
        if (workOrders.isEmpty()) {
            System.out.println("No work orders found.");
        }

        return workOrders;
    }

    public List<WorkOrder> getWorkOrdersWithItemsByStatus(WorkOrderStatus status){
        return workOrderRepo.getWithStatus(status);
    }

    public List<WorkOrder> getCompletedWorkOrdersWithoutInvoices() {
        return workOrderRepo.getCompletedWorkOrdersWithoutInvoices().stream()
                .filter(workOrder -> workOrder.getType() != WorkOrderType.WARRANTY)
                .collect(Collectors.toList());
    }

    public Optional<WorkOrder> getWorkOrderWithItems(int workOrderId) {
        return workOrderRepo.getWithItems(workOrderId);
    }

    public Optional<WorkOrder> getWorkOrder(int id){
        return workOrderRepo.get(id);
    }

    public WorkOrder createWorkOrder(int bookingId,
                                     int mechanicId,
                                     WorkOrderType type,
                                     int... serviceItemIds) {

        if (type == null) {
            //TODO change
            throw new IllegalArgumentException("A work order must have a type.");
        }

        Optional<Booking> optionalBooking = bookingRepo.get(bookingId);

        if (!optionalBooking.isPresent()) {
            throw new BookingNotFoundException(bookingId);
        }
        Booking booking = optionalBooking.get();

        //WorkOrder can in draft have no mechanic and no serviceItems, if mechanic or serviceItem is
        // selected but does not exist, the method throws IllegalArgumentException
        Mechanic mechanic = null;
        if(mechanicId > 0) {
            Optional<Mechanic> optionalMechanic = mechanicRepo.get(mechanicId);

            if (!optionalMechanic.isPresent()) {
                throw new MechanicNotFoundException(mechanicId);
            }
            mechanic = optionalMechanic.get();

            if (!mechanic.isAvailable()) {
                throw new MechanicNotAvailableException("Mechanic " + mechanic.getName() + " is not available.");
            }
        }

        WorkOrder workOrder = new WorkOrder(
                booking,
                mechanic,
                type
        );

        for (int serviceItemId : serviceItemIds) {
            Optional<ServiceItem> optionalServiceItem = serviceItemRepo.get(serviceItemId);
            if (!optionalServiceItem.isPresent()) {
                throw new ServiceItemNotFoundException(serviceItemId);
            }
            ServiceItem serviceItem = optionalServiceItem.get();
            workOrder.addItem(serviceItem, serviceItem.getPrice());
        }


        WorkOrder savedWorkOrder = workOrderRepo.save(workOrder);
        booking.setStatus("WORK_ORDER_CREATED");
        bookingRepo.update(booking);

        System.out.println("Work order created successfully.");
        System.out.println(savedWorkOrder);

        return savedWorkOrder;
    }

    public void confirmWorkOrder (int workOrderId) {
        Optional<WorkOrder> optionalWorkOrder = getWorkOrderWithItems(workOrderId);

        if (!optionalWorkOrder.isPresent()) {
            throw new WorkOrderNotFoundException(workOrderId);
        }

        WorkOrder workOrder = optionalWorkOrder.get();

        if (!workOrder.getStatus().canTransitionTo(WorkOrderStatus.CONFIRMED)) {
            throw new StatusNotValidException("Wrong status on work order. Only draft work orders can be confirmed.");
        }

        if(workOrder.getItems().isEmpty()){
            throw new InputNotValidException("No service items. Work order must contains at least one service item");
        }

        workOrder.changeStatus(WorkOrderStatus.CONFIRMED);
        workOrderRepo.update(workOrder);

        System.out.println("Work order " + workOrderId + " has been confirmed.");
    }

    public WorkOrder updateWorkOrderServices(int workOrderId,
                                             List<Integer> serviceItemIdsToAdd,
                                             List<Integer> itemIdsToRemove) {
        Optional<WorkOrder> optionalWorkOrder = getWorkOrderWithItems(workOrderId);

        if (!optionalWorkOrder.isPresent()) {
            throw new WorkOrderNotFoundException(workOrderId);
        }
        WorkOrder workOrder = optionalWorkOrder.get();

        if (!workOrder.getStatus().canEdit()) {
            throw new StatusNotValidException("Services can only be changed before the work order is started.");
        }

        for (Integer itemId : itemIdsToRemove) {
            if (!workOrder.removeItem(itemId)) {
                throw new NotInListException("Item with ID " + itemId + " is not on the work order.");
            }
        }

        for (Integer serviceItemId : serviceItemIdsToAdd) {
            Optional<ServiceItem> optionalServiceItem = serviceItemRepo.get(serviceItemId);

            if (!optionalServiceItem.isPresent()) {
                throw new ServiceItemNotFoundException(serviceItemId);
            }

            if (workOrder.hasService(serviceItemId)) {
                continue;
            }

            ServiceItem serviceItem = optionalServiceItem.get();
            workOrder.addItem(serviceItem, serviceItem.getPrice());
        }

        return workOrderRepo.update(workOrder);
    }


    public WorkOrder updateWorkOrderType(int workOrderId, WorkOrderType type) {
        if (type == null) {
            throw new InputNotValidException("A work order must have a type.");
        }

        Optional<WorkOrder> optionalWorkOrder = getWorkOrder(workOrderId);

        if (!optionalWorkOrder.isPresent()) {
            throw new WorkOrderNotFoundException(workOrderId);
        }
        WorkOrder workOrder = optionalWorkOrder.get();

        if (!workOrder.getStatus().canEdit()) {
            throw new StatusNotValidException("The type can only be changed before the work order is started.");
        }

        workOrder.setType(type);
        return workOrderRepo.update(workOrder);
    }

    /**
     * Updates which mechanic is connected to work order.
     * Method throws WorkOrderNotFoundException if workOrderId is not present in database.
     * Throws StatusNotValidException if the status of the work order is not in an editable state.
     * Throws MechanicNotFoundException if mechanicId is not present in database.
     * Throws MechanicNotAvailableException if the mechanic is not available.
     * @param workOrderId id of work order to be updated.
     * @param mechanicId id of mechanic to be connected to work order. If value is 0, the mechanic connected to work order will be changed to null.
     * @return updated work order
     */
    public WorkOrder updateWorkOrderMechanic(int workOrderId, int mechanicId){
        Optional<WorkOrder> optionalWorkOrder = getWorkOrder(workOrderId);

        if (!optionalWorkOrder.isPresent()) {
            throw new WorkOrderNotFoundException(workOrderId);
        }
        WorkOrder workOrder = optionalWorkOrder.get();

        if (!workOrder.getStatus().canEdit()) {
            throw new StatusNotValidException("The type can only be changed before the work order is started.");
        }

        Mechanic mechanic = null;
        if(mechanicId > 0) {
            Optional<Mechanic> optionalMechanic = mechanicRepo.get(mechanicId);

            if (!optionalMechanic.isPresent()) {
                throw new MechanicNotFoundException(mechanicId);
            }
            mechanic = optionalMechanic.get();

            if (!mechanic.isAvailable()) {
                throw new MechanicNotAvailableException("Mechanic " + mechanic.getName() + " is not available.");
            }
        }
        workOrder.setMechanic(mechanic);
        return workOrderRepo.update(workOrder);
    }

    public void startWorkOrder(int workOrderId) {
        Optional<WorkOrder> optionalWorkOrder = getWorkOrderWithItems(workOrderId);

        if (!optionalWorkOrder.isPresent()) {
            throw new WorkOrderNotFoundException(workOrderId);
        }
        WorkOrder workOrder = optionalWorkOrder.get();

        if (!workOrder.getStatus().canTransitionTo(WorkOrderStatus.IN_PROGRESS)) {
            throw new StatusNotValidException("Only confirmed work orders can be started.");
        }

        Mechanic mechanic = workOrder.getMechanic();

        if(mechanic == null){
            throw new MechanicNotFoundException("No mechanic. A mechanic must be selected to start the work order.");
        }

        if(workOrder.getItems().isEmpty()){
            throw new InputNotValidException("No service items. Work order must contains at least one service item");
        }

        mechanic.setAvailable(false);

        Booking booking = workOrder.getBooking();

        if (booking != null) {
            booking.setStatus("IN_PROGRESS");
        }

        workOrder.changeStatus(WorkOrderStatus.IN_PROGRESS);
        workOrderRepo.update(workOrder);

        System.out.println("Work order " + workOrderId + " has been started.");
    }

    public void completeWorkOrder(int workOrderId) {
        Optional<WorkOrder> optionalWorkOrder = getWorkOrder(workOrderId);

        if (!optionalWorkOrder.isPresent()) {
            throw new WorkOrderNotFoundException(workOrderId);
        }
        WorkOrder workOrder = optionalWorkOrder.get();

        if (!workOrder.getStatus().canTransitionTo(WorkOrderStatus.COMPLETED)) {
            throw new StatusNotValidException("Only work orders in progress can be completed.");
        }

        Mechanic mechanic = workOrder.getMechanic();
        Booking booking = workOrder.getBooking();

        if (mechanic != null) {
            mechanic.setAvailable(true);
        }

        if (booking != null) {
            booking.setStatus("COMPLETED");
        }

        workOrder.changeStatus(WorkOrderStatus.COMPLETED);
        workOrderRepo.update(workOrder);

        System.out.println("Work order " + workOrderId + " has been completed.");
    }

    public void cancelWorkOrder(int workOrderId) {
        Optional<WorkOrder> optionalWorkOrder = getWorkOrder(workOrderId);

        if (!optionalWorkOrder.isPresent()) {
            throw new WorkOrderNotFoundException(workOrderId);
        }

        WorkOrder workOrder = optionalWorkOrder.get();

        if (!workOrder.getStatus().canBeCancelled()) {
            throw new StatusNotValidException("Work order cannot be cancelled.");
        }

        Mechanic mechanic = workOrder.getMechanic();
        Booking booking = workOrder.getBooking();

        if (workOrder.getStatus() == WorkOrderStatus.IN_PROGRESS && mechanic != null) {
            mechanic.setAvailable(true);
        }

        if (booking != null) {
            booking.setStatus("CANCELLED");
        }

        workOrder.changeStatus(WorkOrderStatus.CANCELLED);
        workOrderRepo.update(workOrder);

        System.out.println("Work order " + workOrderId + " has been cancelled.");
    }
}
