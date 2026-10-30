package com.wac.autocore.service;

import com.wac.autocore.data.*;
import com.wac.autocore.exceptions.*;
import com.wac.autocore.model.*;
import com.wac.autocore.security.AccessGuard;
import com.wac.autocore.security.Permission;

import java.util.List;
import java.util.Optional;

public class WorkOrderService {
    private final BookingRepo bookingRepo;
    private final WorkOrderRepo workOrderRepo;
    private final MechanicRepo mechanicRepo;
    private final ServiceItemRepo serviceItemRepo;
    private final AccessGuard accessGuard;

    public WorkOrderService(BookingRepo bookingRepo, WorkOrderRepo workOrderRepo, MechanicRepo mechanicRepo,
                            ServiceItemRepo serviceItemRepo, AccessGuard accessGuard) {
        this.bookingRepo = bookingRepo;
        this.workOrderRepo = workOrderRepo;
        this.mechanicRepo = mechanicRepo;
        this.serviceItemRepo = serviceItemRepo;
        this.accessGuard = accessGuard;
    }

    public List<WorkOrder> getWorkOrders() {
        accessGuard.require(Permission.READ, "getWorkOrders");
        List<WorkOrder> workOrders = workOrderRepo.getAll();
        if (workOrders.isEmpty()) {
            System.out.println("No work orders found.");
        }

        return workOrders;
    }

    public List<WorkOrder> getWorkOrdersWithItems(){
        accessGuard.require(Permission.READ, "getWorkOrdersWithItems");
        List<WorkOrder> workOrders = workOrderRepo.getAllWithItems();
        if (workOrders.isEmpty()) {
            System.out.println("No work orders found.");
        }

        return workOrders;
    }

    public List<WorkOrder> getWorkOrdersWithItemsByStatus(WorkOrderStatus status){
        accessGuard.require(Permission.READ, "getWorkOrdersWithItemsByStatus");
        return workOrderRepo.getWithStatus(status);
    }

    public Optional<WorkOrder> getWorkOrderWithItems(int workOrderId) {
        accessGuard.require(Permission.READ, "getWorkOrderWithItems");
        if(workOrderId <= 0)
            return Optional.empty();
        return workOrderRepo.getWithItems(workOrderId);
    }

    public Optional<WorkOrder> getWorkOrder(int workOrderId){
        accessGuard.require(Permission.READ, "getWorkOrder");
        if(workOrderId <= 0)
            return Optional.empty();
        return workOrderRepo.get(workOrderId);
    }

    /**
     * Creates a new work order based on the provided parameters. If a mechanic or service items are
     * specified, the method validates their existence and availability. Updates the connected booking
     * status to "WORK_ORDER_CREATED" upon successful creation of the work order.
     *
     * @param bookingId the ID of the booking associated with the new work order.
     * @param mechanicId the ID of the mechanic assigned to the work order. If no mechanic is assigned, pass 0.
     * @param type the type of the work order, cannot be null.
     * @param serviceItemIds IDs of service items to be included in the work order. This parameter accepts multiple values.
     * @return the created work order after saving it to the repository.
     * @throws InputNotValidException if the provided work order type is null.
     * @throws BookingNotFoundException if the booking associated with the given bookingId is not found.
     * @throws MechanicNotFoundException if the mechanic associated with the given mechanicId is not found and mechanicId is greater than 0.
     * @throws MechanicNotAvailableException if the mechanic with the given ID is not available for assignment.
     * @throws ServiceItemNotFoundException if a service item with the given ID does not exist.
     */
    public WorkOrder createWorkOrder(int bookingId,
                                     int mechanicId,
                                     WorkOrderType type,
                                     int... serviceItemIds) {
        accessGuard.require(Permission.MANAGE_WORK_ORDERS, "createWorkOrder");

        if (type == null) {
            throw new InputNotValidException("A work order must have a type.");
        }

        Booking booking = bookingRepo.get(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        //WorkOrder can in draft have no mechanic and no serviceItems, if mechanic or serviceItem is
        // selected but does not exist, the method throws Exception
        Mechanic mechanic = null;
        if(mechanicId > 0) {
            Optional<Mechanic> optionalMechanic = mechanicRepo.get(mechanicId);

            if (optionalMechanic.isEmpty()) {
                throw new MechanicNotFoundException(mechanicId);
            }
            mechanic = optionalMechanic.get();
        }

        WorkOrder workOrder = new WorkOrder(
                booking,
                mechanic,
                type
        );

        for (int serviceItemId : serviceItemIds) {
            Optional<ServiceItem> optionalServiceItem = serviceItemRepo.get(serviceItemId);
            if (optionalServiceItem.isEmpty()) {
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

    /**
     * Confirms a work order by changing its status to "CONFIRMED".
     *
     * @param workOrderId the unique identifier of the work order to be confirmed
     * @throws WorkOrderNotFoundException if the work order with the specified ID does not exist
     * @throws StatusNotValidException if the current status of the work order does not allow transitioning to "CONFIRMED"
     * @throws InputNotValidException if the work order does not contain any service items
     */
    public void confirmWorkOrder (int workOrderId) {
        accessGuard.require(Permission.MANAGE_WORK_ORDERS, "confirmWorkOrder");

        Optional<WorkOrder> optionalWorkOrder = getWorkOrderWithItems(workOrderId);

        if (optionalWorkOrder.isEmpty()) {
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

    /**
     * Updates the services associated with a given work order by adding and/or removing service items.
     *
     * @param workOrderId the unique identifier of the work order to be updated
     * @param serviceItemIdsToAdd a list of service item IDs to be added to the work order
     * @param itemIdsToRemove a list of service item IDs to be removed from the work order
     * @return updated work order
     * @throws WorkOrderNotFoundException if the work order with the specified ID is not found
     * @throws StatusNotValidException if the work order's status is not editable
     * @throws NotInListException if one or more service items to remove are not present in the work order
     * @throws ServiceItemNotFoundException if one or more service items to add are not found
     */
    public WorkOrder updateWorkOrderServices(int workOrderId,
                                             List<Integer> serviceItemIdsToAdd,
                                             List<Integer> itemIdsToRemove) {
        accessGuard.require(Permission.MANAGE_WORK_ORDERS, "updateWorkOrderServices");

        Optional<WorkOrder> optionalWorkOrder = getWorkOrderWithItems(workOrderId);

        if (optionalWorkOrder.isEmpty()) {
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

            if (optionalServiceItem.isEmpty()) {
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


    /**
     * Updates the type of work order with the specified ID.
     *
     * @param workOrderId the unique identifier of the work order to be updated
     * @param type the new type to assign to the work order
     * @return the updated work order after applying the new type
     * @throws InputNotValidException if the provided type is null
     * @throws WorkOrderNotFoundException if no work order is found with the specified ID
     * @throws StatusNotValidException if the work order status does not allow modifications to its type
     */
    public WorkOrder updateWorkOrderType(int workOrderId, WorkOrderType type) {
        accessGuard.require(Permission.MANAGE_WORK_ORDERS, "updateWorkOrderType");

        if (type == null) {
            throw new InputNotValidException("A work order must have a type.");
        }

        Optional<WorkOrder> optionalWorkOrder = getWorkOrder(workOrderId);

        if (optionalWorkOrder.isEmpty()) {
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
     *
     * @param workOrderId id of work order to be updated.
     * @param mechanicId id of mechanic to be connected to work order. If value is 0, the mechanic connected to work order will be changed to null.
     * @return updated work order
     * @throws WorkOrderNotFoundException if workOrderId is not present in database.
     * @throws StatusNotValidException if the status of the work order is not in an editable state.
     * @throws MechanicNotFoundException if mechanicId is not present in database.
     * @throws MechanicNotAvailableException if the mechanic is not available.
     */
    public WorkOrder updateWorkOrderMechanic(int workOrderId, int mechanicId){
        accessGuard.require(Permission.MANAGE_WORK_ORDERS, "updateWorkOrderMechanic");

        Optional<WorkOrder> optionalWorkOrder = getWorkOrder(workOrderId);

        if (optionalWorkOrder.isEmpty()) {
            throw new WorkOrderNotFoundException(workOrderId);
        }
        WorkOrder workOrder = optionalWorkOrder.get();

        if (!workOrder.getStatus().canEdit()) {
            throw new StatusNotValidException("The mechanic can only be changed before the work order is started.");
        }

        Mechanic mechanic = null;
        if(mechanicId > 0) {
            Optional<Mechanic> optionalMechanic = mechanicRepo.get(mechanicId);

            if (optionalMechanic.isEmpty()) {
                throw new MechanicNotFoundException(mechanicId);
            }
            mechanic = optionalMechanic.get();
        }
        workOrder.setMechanic(mechanic);
        return workOrderRepo.update(workOrder);
    }

    /**
     * Starts a work order by transitioning its status to "IN_PROGRESS" and updates the availability
     * of the assigned mechanic and marks the connected booking as "IN_PROGRESS".
     *
     * @param workOrderId the unique identifier of the work order to be started
     * @throws WorkOrderNotFoundException if the work order with the specified ID does not exist
     * @throws StatusNotValidException if the current status of the work order does not allow transitioning to "IN_PROGRESS"
     * @throws MechanicNotFoundException if no mechanic is assigned to the work order
     * @throws InputNotValidException if the work order does not contain any service items
     */
    public void startWorkOrder(int workOrderId) {
        accessGuard.require(Permission.MANAGE_WORK_ORDERS, "startWorkOrder");

        Optional<WorkOrder> optionalWorkOrder = getWorkOrderWithItems(workOrderId);

        if (optionalWorkOrder.isEmpty()) {
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

        if (!mechanic.isAvailable()) {
            throw new MechanicNotAvailableException("Mechanic " + mechanic.getName() + " is not available.");
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

    /**
     * Completes a work order by transitioning its status to "COMPLETED". Marks the connected mechanic
     * as available and updates the booking status to "COMPLETED". If no mechanic respectively mechanic is
     * connected, the method will not throw any exceptions.
     *
     * @param workOrderId the unique identifier of the work order to be completed
     * @throws WorkOrderNotFoundException if the work order with the specified ID does not exist
     * @throws StatusNotValidException if the current work order status does not allow transitioning to "COMPLETED"
     */
    public void completeWorkOrder(int workOrderId) {
        accessGuard.require(Permission.MANAGE_WORK_ORDERS, "completeWorkOrder");

        Optional<WorkOrder> optionalWorkOrder = getWorkOrder(workOrderId);

        if (optionalWorkOrder.isEmpty()) {
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

    /**
     * Cancels the work order with the specified ID. Updates the status of the work order to
     * "CANCELLED". If the work order is "IN_PROGRESS" and has an assigned mechanic, the mechanic's
     * availability is updated. Additionally, the connected booking's status is set to "CANCELLED",
     * if applicable.
     *
     * @param workOrderId the unique identifier of the work order to be cancelled
     * @throws WorkOrderNotFoundException if the work order with the specified ID is not found
     * @throws StatusNotValidException if the current status of the work order does not allow it to be cancelled
     */
    public void cancelWorkOrder(int workOrderId) {
        accessGuard.require(Permission.MANAGE_WORK_ORDERS, "cancelWorkOrder");

        Optional<WorkOrder> optionalWorkOrder = getWorkOrder(workOrderId);

        if (optionalWorkOrder.isEmpty()) {
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