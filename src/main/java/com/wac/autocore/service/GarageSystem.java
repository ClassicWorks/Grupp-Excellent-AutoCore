package com.wac.autocore.service;

import com.wac.autocore.data.*;
import com.wac.autocore.exceptions.*;
import com.wac.autocore.model.*;
import com.wac.autocore.service.discount.Discount;
import com.wac.autocore.service.discount.DiscountCodes;
import com.wac.autocore.service.discount.PercentageDiscount;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GarageSystem {
    private final VehicleRepo vehicleRepo;
    private final CustomerRepo customerRepo;
    private final MechanicRepo mechanicRepo;
    private final ServiceItemRepo serviceItemRepo;
    private final BookingRepo bookingRepo;
    private final WorkOrderRepo workOrderRepo;
    private final InvoiceRepo invoiceRepo;
    private final ServicePackRepo servicePackRepo;
    private static final Discount VIP_DISCOUNT = new PercentageDiscount(0.10);

    private final CustomerService customerService;
    private final PaymentService paymentService;
    private final VehicleService vehicleService;
    private final BookingService bookingService;
    private final WorkOrderService workOrderService;

    public GarageSystem() {
        vehicleRepo = new VehicleRepoImpl();
        customerRepo = new CustomerRepoImpl();
        mechanicRepo = new MechanicRepoImpl();
        serviceItemRepo = new ServiceItemRepoImpl();
        bookingRepo = new BookingRepoImpl();
        workOrderRepo = new WorkOrderRepoImpl();
        invoiceRepo = new InvoiceRepoImpl();
        servicePackRepo = new ServicePackRepoImpl();

        customerService = new CustomerService(customerRepo, vehicleRepo);
        paymentService = new PaymentService(new PaymentRepoImpl(), invoiceRepo);
        vehicleService = new VehicleService(vehicleRepo, customerRepo, bookingRepo);
        workOrderService = new WorkOrderService(bookingRepo, workOrderRepo, mechanicRepo, serviceItemRepo);
        bookingService = new BookingService(vehicleRepo, bookingRepo);
    }

    public void showCustomers() {
        System.out.println();
        System.out.println("=== CUSTOMERS ===");

        if (customerRepo.getAll().isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        for (Customer customer : customerRepo.getAll()) {
            System.out.println(customer);
        }
    }

    public List<Customer> getCustomers() {
        return customerService.getCustomers();
    }

    public Optional<Customer> getCustomer(int id){
        return customerService.getCustomer(id);
    }


    public List<Customer> getCustomerMatching(String query) {
        return customerService.getCustomerMatching(query);
    }

    public void showVehicles() {
        vehicleService.showVehicles();
    }

    public List<Vehicle> getVehicles() {
        return vehicleService.getVehicles();
    }

    public Optional<Vehicle> getVehicle(int id){
        return vehicleService.getVehicle(id);
    }

    public List<Vehicle> getCustomersVehicle(int customerId){
        return vehicleService.getCustomersVehicle(customerId);
    }

    public void showBookings() {
        bookingService.showBookings();
    }

    public List<Booking> getBookings() {
        return bookingService.getBookings();
    }

    public Optional<Booking> getBooking(int id){
        return bookingService.getBooking(id);
    }

    public void showServiceItems() {
        System.out.println();
        System.out.println("=== SERVICES ===");

        if (serviceItemRepo.getAll().isEmpty()) {
            System.out.println("No services found.");
            return;
        }

        for (ServiceItem serviceItem : serviceItemRepo.getAll()) {
            System.out.println(serviceItem);
        }
    }

    public List<ServiceItem> getServiceItems() {
        List<ServiceItem> serviceItems = serviceItemRepo.getAll();
        if (serviceItems.isEmpty()) {
            System.out.println("No services found.");
        }

        return serviceItems;
    }

    public List<ServiceItem> getServiceItemsByIds(List<Integer> serviceIds) {
        List<ServiceItem> serviceItems = serviceItemRepo.getByIds(serviceIds);
        if (serviceItems.isEmpty()) {
            System.out.println("No services found.");
        }

        return serviceItems;
    }


    public List<Integer> getMissingIds(List<Integer> lookingForIds, List<ServiceItem> serviceList) {
        Set<Integer> foundIds = serviceList.stream()
                .map(ServiceItem::getId)
                .collect(Collectors.toSet());

        return lookingForIds.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toList());
    }

    public Optional<ServiceItem> getServiceItem(int id){
        return serviceItemRepo.get(id);
    }

    public ServiceItem updateServiceItemPrice(int serviceItemId, double newPrice) {
        if (newPrice < 0) {
            throw new InputNotValidException("Price can not  be negative");
        }

        Optional<ServiceItem> optionalServiceItem = getServiceItem(serviceItemId);

        if (!optionalServiceItem.isPresent()) {
            throw new ServiceItemNotFoundException(serviceItemId);
        }

        ServiceItem serviceItem = optionalServiceItem.get();

        serviceItem.setPrice(newPrice);
        ServiceItem savedServiceItem = serviceItemRepo.update(serviceItem);

        System.out.println("Price for " + savedServiceItem.getName() + " updated to " + newPrice + " SEK.");

        return savedServiceItem;
    }

    public Customer updateCustomer(int customerId, String name, String phone, String email) {
        return customerService.updateCustomer(customerId, name, phone, email);
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

    public Vehicle updateVehicle(int vehicleId, String registrationNumber, String brand,
                                 String model, int year, Integer customerId) {
       return vehicleService.updateVehicle(vehicleId, registrationNumber, brand, model, year, customerId);
    }

    public boolean customerHasVehicles(int customerId) {
        return customerService.customerHasVehicles(customerId);
    }

    public boolean vehicleHasBookings(int vehicleId) {
        return vehicleService.vehicleHasBookings(vehicleId);
    }

    public boolean mechanicHasUncompletedWorkOrders(int mechanicId) {
        return workOrderRepo.mechanicHasUncompletedWorkOrders(mechanicId);
    }

    public boolean deleteCustomer(int customerId) {
        return customerService.deleteCustomer(customerId);
    }

    //TODO om alla bokningar är klar/avbrutna, koppla lös dem
    public boolean deleteVehicle(int vehicleId) {
        return vehicleService.deleteVehicle(vehicleId);
    }

    //TODO implementera UnitOfWork då när man tar bort en mekaniker ska den koppla bort den från existerande workOrders (som ej är klar/avbrutna)
    public boolean deleteMechanic(int mechanicId) {
        if (!mechanicRepo.get(mechanicId).isPresent()) {
            throw new MechanicNotFoundException(mechanicId);
        }
        //If mechanic has work assigned they cannot be deleted. Connected WorkOrders need to be removed before
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

    public void showServicePacks() {
        System.out.println();
        System.out.println("=== SERVICE PACKS ===");
        if (servicePackRepo.getAll().isEmpty()) {
            System.out.println("No services found.");
            return;
        }
        for (ServicePack servicePack : servicePackRepo.getAll()) {
            System.out.println(servicePack);
        }
    }

    public List<ServicePack> getServicePacks() {
        return servicePackRepo.getAll();
    }

    public Optional<ServicePack> getServicePack(int id){
        return servicePackRepo.getById(id);
    }

    //TODO get values and Garagesystem check
    public ServicePack updateServicePack(int servicePackId, ServicePack updatedServicePack){
        Optional<ServicePack> optionalServicePack = getServicePack(servicePackId);
        if(!optionalServicePack.isPresent()){
            throw new ServicePackNotFoundException(servicePackId);
        }

        //Om någon ServiceItem inte finns kasta exception
        updatedServicePack.getServiceItems().forEach(si -> {
            Optional<ServiceItem> optionalServiceItem = getServiceItem(si.getId());
            if(!optionalServiceItem.isPresent()) {
                throw new ServiceItemNotFoundException(si.getId());
            }
        });

        ServicePack servicePackToUpdate = optionalServicePack.get();
        servicePackToUpdate.setName(updatedServicePack.getName());
        servicePackToUpdate.setServiceItems(updatedServicePack.getServiceItems());

        return servicePackRepo.update(servicePackToUpdate);
    }

    public void deleteServicePack(int id){
        servicePackRepo.delete(id);
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

    public void showWorkOrders() {
        workOrderService.showWorkOrders();
    }

    public List<WorkOrder> getWorkOrders() {
        return workOrderService.getWorkOrders();
    }

    public List<WorkOrder> getWorkOrdersWithItems(){
        return workOrderService.getWorkOrdersWithItems();
    }

    public List<WorkOrder> getWorkOrdersWithItemsByStatus(WorkOrderStatus status){
        return workOrderService.getWorkOrdersWithItemsByStatus(status);
    }

    public List<WorkOrder> getCompletedWorkOrdersWithoutInvoices() {
        return workOrderService.getCompletedWorkOrdersWithoutInvoices();
    }

    public Optional<WorkOrder> getWorkOrderWithItems(int workOrderId) {
        return workOrderService.getWorkOrderWithItems(workOrderId);
    }

    public Optional<WorkOrder> getWorkOrder(int id){
        return workOrderService.getWorkOrder(id);
    }

    public void showInvoices() {
        System.out.println();
        System.out.println("=== INVOICES ===");

        List<Invoice> invoices = invoiceRepo.getAll();
        if (invoices.isEmpty()) {
            System.out.println("No invoices found.");
            return;
        }

        for (Invoice invoice : invoices) {
            System.out.println(invoice);
        }
    }

    public List<Invoice> getInvoices(){
        List<Invoice> invoices = invoiceRepo.getAll();
        if (invoices.isEmpty()) {
            System.out.println("No invoices found.");
        }
        return invoices;
    }

    public Optional<Invoice> getInvoice(int id){
        return invoiceRepo.get(id);
    }

    public List<Payment> getPayments() {
        return paymentService.getPayments();
    }

    public List<Invoice> getUnpaidInvoices(){
        return invoiceRepo.getWherePayedIs(false);
    }

    public List<Invoice> getPaidInvoices(){
        return invoiceRepo.getWherePayedIs(true);
    }

    public Customer createCustomer(String name, String phone, String email) {
        return customerService.createCustomer(name, phone, email);
    }

    public Vehicle createVehicle(String registrationNumber,
                                 String brand,
                                 String model,
                                 int year,
                                 int customerId) {
        return vehicleService.createVehicle(registrationNumber, brand, model, year, customerId);
    }

    public Booking createBooking(int vehicleId,
                                 LocalDate date,
                                 String description) {
        return bookingService.createBooking(vehicleId, date, description);
    }

    public Booking createBookingWithTranslation(int vehicleId,
                                                LocalDate date,
                                                String description,
                                                String descriptionLang) {
        return bookingService.createBookingWithTranslation(vehicleId, date, description, descriptionLang);
    }


    public Booking createBookingFrom(int previousBookingId) {
        return bookingService.createBookingFrom(previousBookingId);
    }

    public ServicePack createServicePack(String name, int... serviceItemIds){
        if(!servicePackRepo.nameAvailable(name)){
            throw new InputNotValidException("Service pack with name \"" + name + "\" already exists.");
        }
        ServicePack servicePack = new ServicePack(name);

        for(int serviceItemId : serviceItemIds) {
            Optional<ServiceItem> optionalServiceItem = getServiceItem(serviceItemId);
            if(!optionalServiceItem.isPresent()) {
                throw new ServiceItemNotFoundException(serviceItemId);
            }
            ServiceItem serviceItem = optionalServiceItem.get();

            servicePack.addServiceItem(serviceItem);
        }

        return servicePackRepo.save(servicePack);
    }

    public WorkOrder createWorkOrder(int bookingId,
                                     int mechanicId,
                                     WorkOrderType type,
                                     int... serviceItemIds) {
        return workOrderService.createWorkOrder(bookingId, mechanicId, type, serviceItemIds);
    }

    public void confirmWorkOrder (int workOrderId){
        workOrderService.confirmWorkOrder(workOrderId);
    }

    public WorkOrder updateWorkOrderServices(int workOrderId,
                                             List<Integer> serviceItemIdsToAdd,
                                             List<Integer> itemIdsToRemove) {
        return workOrderService.updateWorkOrderServices(workOrderId, serviceItemIdsToAdd, itemIdsToRemove);
    }


    public WorkOrder updateWorkOrderType(int workOrderId, WorkOrderType type) {
        return workOrderService.updateWorkOrderType(workOrderId, type);
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
        return workOrderService.updateWorkOrderMechanic(workOrderId, mechanicId);
    }

    public void startWorkOrder(int workOrderId) {
        workOrderService.startWorkOrder(workOrderId);
    }

    public void completeWorkOrder(int workOrderId) {
        workOrderService.completeWorkOrder(workOrderId);
    }

    public void cancelWorkOrder(int workOrderId) {
        workOrderService.cancelWorkOrder(workOrderId);
    }

    public Invoice createInvoice(int workOrderId, String discountCode) {
        Optional<WorkOrder> optionalWorkOrder = getWorkOrderWithItems(workOrderId);

        if (!optionalWorkOrder.isPresent()) {
            throw new WorkOrderNotFoundException(workOrderId);
        }
        WorkOrder workOrder = optionalWorkOrder.get();

        if (workOrder.getStatus() != WorkOrderStatus.COMPLETED) {
            throw new StatusNotValidException("Invoice can only be created for a completed work order.");
        }

        if (workOrder.getType() == WorkOrderType.WARRANTY) {
            throw new StatusNotValidException("Warranty work is not invoiced.");
        }

        boolean alreadyInvoiced = invoiceRepo.getAll().stream()
                .anyMatch(invoice -> invoice.getWorkOrder().getId() == workOrderId);
        if (alreadyInvoiced) {
            throw new StatusNotValidException("Work order " + workOrderId + " already has an invoice.");
        }

        double amount = 0.0;

        for (WorkOrderItem item : workOrder.getItems()) {
            amount += item.getPriceAtOrder();
        }

        List<Discount> discounts = new ArrayList<>();

        Booking booking = workOrder.getBooking();
        if (booking != null
                && booking.getVehicle() != null
                && booking.getVehicle().getCustomer() != null
                && booking.getVehicle().getCustomer().isVip()) {
            discounts.add(VIP_DISCOUNT);
        }

        if (discountCode != null && !discountCode.trim().isEmpty()) {
            Optional<Discount> codeDiscount = DiscountCodes.find(discountCode);
            if (codeDiscount.isPresent()) {
                discounts.add(codeDiscount.get());
            } else {
                System.out.println("Unknown discount code. No code discount applied.");
            }
        }

        double discount = 0.0;
        for (Discount d : discounts) {
            discount += d.calculate(amount);
        }

        discount = Math.min(discount, amount);

        Invoice invoice = new Invoice(
                workOrder,
                LocalDate.now(),
                amount
        );

        invoice.setDiscount(discount);

        Invoice savedInvoice = invoiceRepo.save(invoice);

        System.out.println("Invoice created successfully.");
        System.out.println(savedInvoice);

        System.out.println("Sending invoice notification to customer...");
        System.out.println("Notification sent.");

        return savedInvoice;
    }

    public Payment processPayment(int invoiceId, String paymentType) {
        return paymentService.processPayment(invoiceId, paymentType);
    }
}