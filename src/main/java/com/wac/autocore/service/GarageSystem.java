package com.wac.autocore.service;

import com.wac.autocore.data.*;
import com.wac.autocore.exceptions.*;
import com.wac.autocore.model.*;
import com.wac.autocore.service.discount.Discount;
import com.wac.autocore.service.discount.DiscountCodes;
import com.wac.autocore.service.discount.PercentageDiscount;
import com.wac.autocore.service.payment.PaymentMethod;
import com.wac.autocore.service.payment.PaymentMethodFactory;
import com.wac.autocore.util.TranslationService;

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
    private final PaymentRepo paymentRepo;
    private final ServicePackRepo servicePackRepo;
    private static final Discount VIP_DISCOUNT = new PercentageDiscount(0.10);

    private final CustomerService customerService;

    public GarageSystem() {
        vehicleRepo = new VehicleRepoImpl();
        customerRepo = new CustomerRepoImpl();
        mechanicRepo = new MechanicRepoImpl();
        serviceItemRepo = new ServiceItemRepoImpl();
        bookingRepo = new BookingRepoImpl();
        workOrderRepo = new WorkOrderRepoImpl();
        invoiceRepo = new InvoiceRepoImpl();
        paymentRepo  = new PaymentRepoImpl();
        servicePackRepo = new ServicePackRepoImpl();

        customerService = new CustomerService(customerRepo, vehicleRepo);
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
        System.out.println();
        System.out.println("=== VEHICLES ===");

        if (vehicleRepo.getAll().isEmpty()) {
            System.out.println("No vehicles found.");
            return;
        }

        for (Vehicle vehicle : vehicleRepo.getAll()) {
            System.out.println(vehicle);
        }
    }

    public List<Vehicle> getVehicles() {
        List<Vehicle> vehicles = vehicleRepo.getAll();
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles found.");
        }
        return vehicles;
    }

    public Optional<Vehicle> getVehicle(int id){
        return vehicleRepo.get(id);
    }

    public List<Vehicle> getCustomersVehicle(int customerId){
        return vehicleRepo.getVehicleByCustomerId(customerId);
    }

    public void showBookings() {
        System.out.println();
        System.out.println("=== BOOKINGS ===");

        if (bookingRepo.getAll().isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Booking booking : bookingRepo.getAll()) {
            System.out.println(booking);
        }
    }

    public List<Booking> getBookings() {
        List<Booking> bookings = bookingRepo.getAll();
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
        }

        return bookings;
    }

    public Optional<Booking> getBooking(int id){
        return bookingRepo.get(id);
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
        if (registrationNumber == null || registrationNumber.trim().isEmpty()
                || model == null || model.trim().isEmpty()) {
            throw new InputNotValidException("Registration number and model cannot be empty.");
        }

        Optional<Vehicle> optionalVehicle = getVehicle(vehicleId);

        if (!optionalVehicle.isPresent()) {
            throw new VehicleNotFoundException(vehicleId);
        }

        Customer customer = null;
        if (customerId != null) {
            Optional<Customer> optionalCustomer = getCustomer(customerId);

            if (!optionalCustomer.isPresent()) {
                throw new CustomerNotFoundException(customerId);
            }
            customer = optionalCustomer.get();
        }

        Vehicle vehicle = optionalVehicle.get();
        vehicle.setRegistrationNumber(registrationNumber.trim());
        vehicle.setBrand(brand == null ? "" : brand.trim());
        vehicle.setModel(model.trim());
        vehicle.setYear(year);
        vehicle.setCustomer(customer);

        return vehicleRepo.update(vehicle);
    }

    public boolean customerHasVehicles(int customerId) {
        return customerService.customerHasVehicles(customerId);
    }

    public boolean vehicleHasBookings(int vehicleId) {
        return bookingRepo.vehicleHasBooking(vehicleId);
    }

    public boolean mechanicHasUncompletedWorkOrders(int mechanicId) {
        return workOrderRepo.mechanicHasUncompletedWorkOrders(mechanicId);
    }

    public boolean deleteCustomer(int customerId) {
        return customerService.deleteCustomer(customerId);
    }

    //TODO om alla bokningar är klar/avbrutna, koppla lös dem
    public boolean deleteVehicle(int vehicleId) {
        if (!vehicleRepo.get(vehicleId).isPresent()) {
            throw new VehicleNotFoundException(vehicleId);
        }
        if (vehicleHasBookings(vehicleId)) {
            throw new DeletionNotAllowedException("Vehicle has bookings and cannot be deleted.");
        }
        vehicleRepo.delete(vehicleId);
        return true;
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

    public void showPayments() {
        System.out.println();
        System.out.println("=== PAYMENTS ===");

        List<Payment> payments = paymentRepo.getAll();
        if (payments.isEmpty()) {
            System.out.println("No payments found.");
            return;
        }

        for (Payment payment : payments) {
            System.out.println(payment);
        }
    }

    public List<Payment> getPayments(){
        List<Payment> payments = paymentRepo.getAll();
        if (payments.isEmpty()) {
            System.out.println("No payments found.");
        }

        return payments;
    }

    public List<Invoice> getUnpaidInvoices(){
        return invoiceRepo.getWherePayedIs(false);
    }

    public List<Invoice> getPaidInvoices(){
        return invoiceRepo.getWherePayedIs(true);
    }

    public Optional<Payment> getPayment(int id){
        return paymentRepo.get(id);
    }

    public Customer createCustomer(String name, String phone, String email) {
        return customerService.createCustomer(name, phone, email);
    }

    public Vehicle createVehicle(String registrationNumber,
                                 String brand,
                                 String model,
                                 int year,
                                 int customerId) {
        if(registrationNumber == null || registrationNumber.trim().isEmpty()){
            throw new InputNotValidException("Vehicle registration number cannot be empty.");
        }
        if(brand == null || brand.trim().isEmpty()){
            throw new InputNotValidException("Vehicle brand cannot be empty.");
        }
        if(model == null || model.trim().isEmpty()){
            throw new InputNotValidException("Vehicle model cannot be empty.");
        }
        if(year <= 1649 || year > LocalDate.now().getYear()){
            throw new InputNotValidException("Vehicle year cannot be before 1649 or efter current year.");
        }
        if(customerId <= 0){
            throw new InputNotValidException("Customer ID cannot be zero or negative.");
        }

        Optional<Customer> optionalCustomer = getCustomer(customerId);

        if (!optionalCustomer.isPresent()) {
            throw new CustomerNotFoundException(customerId);
        }
        Customer customer = optionalCustomer.get();


        Vehicle vehicle = new Vehicle(
                registrationNumber,
                brand,
                model,
                year,
                customer
        );

        Vehicle savedVehicle = vehicleRepo.save(vehicle);

        System.out.println("Vehicle created successfully.");
        System.out.println(savedVehicle);

        return savedVehicle;
    }

    public Booking createBooking(int vehicleId,
                                 LocalDate date,
                                 String description) {
        if(vehicleId <= 0){
            throw new InputNotValidException("Vehicle ID cannot be zero or negative.");
        }
        if(date == null){
            throw new InputNotValidException("Booking date cannot be empty.");
        }
        if(description == null || description.trim().isEmpty() || description.length() > 255){
            throw new InputNotValidException("Booking description cannot be empty or more that 255 characters.");
        }
        Optional<Vehicle> optionalVehicle = getVehicle(vehicleId);

        if (!optionalVehicle.isPresent()) {
            throw new VehicleNotFoundException(vehicleId);
        }

        Booking booking = new Booking(
                optionalVehicle.get(),
                date,
                description
        );

        Booking savedBooking = bookingRepo.save(booking);

        System.out.println("Booking created successfully.");
        System.out.println(savedBooking);

        return savedBooking;
    }

    public Booking createBookingWithTranslation(int vehicleId,
                                                LocalDate date,
                                                String description,
                                                String descriptionLang) {
        if(vehicleId <= 0){
            throw new InputNotValidException("Vehicle ID cannot be zero or negative.");
        }
        if(date == null){
            throw new InputNotValidException("Booking date cannot be empty.");
        }
        if(description == null || description.trim().isEmpty() || description.length() > 255){
            throw new InputNotValidException("Booking description cannot be empty or more that 255 characters.");
        }
        if(descriptionLang == null || descriptionLang.trim().isEmpty() || descriptionLang.length() > 5){
            throw new InputNotValidException("Description language can not be empty or more than 5 characters.");
        }

        Optional<Vehicle> optionalVehicle = getVehicle(vehicleId);

        if (!optionalVehicle.isPresent()) {
           throw new VehicleNotFoundException(vehicleId);
        }

        String targetLang = "sv".equals(descriptionLang) ? "en" : "sv";
        String translated = TranslationService.translate(description, descriptionLang, targetLang);

        Booking booking = new Booking(
                optionalVehicle.get(),
                date,
                description,
                descriptionLang,
                translated
        );

        Booking savedBooking = bookingRepo.save(booking);

        System.out.println("Booking created successfully.");
        System.out.println(savedBooking);

        return savedBooking;
    }


    public ServicePack createServicePack(String name, int... serviceItemIds) throws IllegalArgumentException {
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

    public Booking createBookingFrom(int previousBookingId) {
        Optional<Booking> optionalPrevious = getBooking(previousBookingId);

        if (!optionalPrevious.isPresent()) {
            throw new BookingNotFoundException(previousBookingId);
        }
        Booking previous = optionalPrevious.get();

        if (previous.getVehicle() == null) {
            throw new VehicleNotFoundException("Booking with ID " + previousBookingId + " has no vehicle.");
        }

        Booking booking = new Booking(
                previous.getVehicle(),
                LocalDate.now(),
                previous.getDescription(),
                previous.getDescriptionLang(),
                previous.getDescriptionTranslated()
        );

        Booking savedBooking = bookingRepo.save(booking);

        System.out.println("Booking created from booking " + previousBookingId + ".");
        System.out.println(savedBooking);

        return savedBooking;
    }

    public WorkOrder createWorkOrder(int bookingId,
                                     int mechanicId,
                                     WorkOrderType type,
                                     int... serviceItemIds)
    throws IllegalArgumentException{

        if (type == null) {
            //TODO change
            throw new IllegalArgumentException("A work order must have a type.");
        }

        Optional<Booking> optionalBooking = getBooking(bookingId);

        if (!optionalBooking.isPresent()) {
            throw new BookingNotFoundException(bookingId);
        }
        Booking booking = optionalBooking.get();

        //WorkOrder can in draft have no mechanic and no serviceItems, if mechanic or serviceItem is
        // selected but does not exist, the method throws IllegalArgumentException
        Mechanic mechanic = null;
        if(mechanicId > 0) {
            Optional<Mechanic> optionalMechanic = getMechanic(mechanicId);

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
            Optional<ServiceItem> optionalServiceItem = getServiceItem(serviceItemId);
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

    public void confirmWorkOrder (int workOrderId) throws IllegalArgumentException {
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
                                             List<Integer> itemIdsToRemove)
    throws WorkOrderNotFoundException, StatusNotValidException, NotInListException, ServiceItemNotFoundException {
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
            Optional<ServiceItem> optionalServiceItem = getServiceItem(serviceItemId);

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


    public WorkOrder updateWorkOrderType(int workOrderId, WorkOrderType type)
            throws IllegalArgumentException {
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
            Optional<Mechanic> optionalMechanic = getMechanic(mechanicId);

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
        Optional<Invoice> optionalInvoice = getInvoice(invoiceId);

        if (!optionalInvoice.isPresent()) {
            throw new InvoiceNotFoundException(invoiceId);
        }
        Invoice invoice = optionalInvoice.get();

        if (invoice.isPaid()) {
            throw new StatusNotValidException("Invoice has already been paid.");
        }

        Payment payment = new Payment(
                invoice,
                invoice.getTotalAmount(),
                paymentType
        );

        boolean successful = false;

        Optional<PaymentMethod> paymentMethod = PaymentMethodFactory.create(paymentType);

        if (paymentMethod.isPresent()) {
            successful = paymentMethod.get().pay(invoice.getTotalAmount());
        } else {
            throw new InputNotValidException("Unknown payment type: " + paymentType);
        }

        payment.setSuccessful(successful);
        Payment savedPayment = paymentRepo.save(payment);

        if (successful) {
            invoice.setPaid(true);
            invoiceRepo.update(invoice);

            System.out.println("Payment completed successfully.");
            System.out.println("Sending payment confirmation to customer...");
            System.out.println("Confirmation sent.");
        } else {
            System.out.println("Payment failed.");
        }

        return savedPayment;
    }
}