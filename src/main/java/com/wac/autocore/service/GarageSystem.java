package com.wac.autocore.service;

import com.wac.autocore.data.*;
import com.wac.autocore.model.*;
import com.wac.autocore.util.TranslationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        if (customerRepo.getAll().isEmpty()) {
            System.out.println("No customers found.");
            return new ArrayList<>();
        }

        return customerRepo.getAll();
    }

    public Optional<Customer> getCustomer(int id){
        return customerRepo.get(id);
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
        if (vehicleRepo.getAll().isEmpty()) {
            System.out.println("No vehicles found.");
            return new ArrayList<>();
        }

        return vehicleRepo.getAll();
    }

    public Optional<Vehicle> getVehicle(int id){
        return vehicleRepo.get(id);
    }

    public List<Vehicle> getCustomersVehicle(int customerId){
        return getVehicles().stream()
                .filter(v -> customerId == v.getCustomer().getId())
                .collect(Collectors.toList());
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
            return new ArrayList<>();
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
            return new ArrayList<>();
        }

        return serviceItems;
    }

    public Optional<ServiceItem> getServiceItem(int id){
        return serviceItemRepo.get(id);
    }

    public ServiceItem updateServiceItemPrice(int serviceItemId, double newPrice) {
        if (newPrice < 0) {
            System.out.println("Price cannot be negative.");
            return null;
        }

        Optional<ServiceItem> optionalServiceItem = getServiceItem(serviceItemId);

        if (!optionalServiceItem.isPresent()) {
            System.out.println("Service item with ID " + serviceItemId + " does not exist.");
            return null;
        }

        ServiceItem serviceItem = optionalServiceItem.get();

        serviceItem.setPrice(newPrice);
        ServiceItem savedServiceItem = serviceItemRepo.update(serviceItem);

        System.out.println("Price for " + savedServiceItem.getName() + " updated to " + newPrice + " SEK.");

        return savedServiceItem;
    }

    public Customer updateCustomer(int customerId, String name, String phone, String email) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Customer name cannot be empty.");
            return null;
        }

        Optional<Customer> optionalCustomer = getCustomer(customerId);

        if (!optionalCustomer.isPresent()) {
            System.out.println("Customer with ID " + customerId + " does not exist.");
            return null;
        }

        Customer customer = optionalCustomer.get();
        customer.setName(name.trim());
        customer.setPhone(phone);
        customer.setEmail(email);

        return customerRepo.update(customer);
    }

    public Mechanic updateMechanic(int mechanicId, String name, String phone, String specialization) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Mechanic name cannot be empty.");
            return null;
        }

        Optional<Mechanic> optionalMechanic = getMechanic(mechanicId);

        if (!optionalMechanic.isPresent()) {
            System.out.println("Mechanic with ID " + mechanicId + " does not exist.");
            return null;
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
            System.out.println("Registration number and model cannot be empty.");
            return null;
        }

        Optional<Vehicle> optionalVehicle = getVehicle(vehicleId);

        if (!optionalVehicle.isPresent()) {
            System.out.println("Vehicle with ID " + vehicleId + " does not exist.");
            return null;
        }

        Customer customer = null;
        if (customerId != null) {
            Optional<Customer> optionalCustomer = getCustomer(customerId);

            if (!optionalCustomer.isPresent()) {
                System.out.println("Customer with ID " + customerId + " does not exist.");
                return null;
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
        for (Vehicle v : vehicleRepo.getAll()) {
            if (v.getCustomer() != null && v.getCustomer().getId() == customerId) {
                return true;
            }
        }
        return false;
    }

    public boolean vehicleHasBookings(int vehicleId) {
        for (Booking b : bookingRepo.getAll()) {
            if (b.getVehicle() != null && b.getVehicle().getId() == vehicleId) {
                return true;
            }
        }
        return false;
    }

    public boolean mechanicHasWorkOrders(int mechanicId) {
        for (WorkOrder w : workOrderRepo.getAll()) {
            if (w.getMechanic() != null && w.getMechanic().getId() == mechanicId) {
                return true;
            }
        }
        return false;
    }

    public boolean deleteCustomer(int customerId) {
        if (!customerRepo.get(customerId).isPresent()) {
            System.out.println("Customer not found.");
            return false;
        }
        if (customerHasVehicles(customerId)) {
            System.out.println("Customer has vehicles and cannot be deleted.");
            return false;
        }
        customerRepo.delete(customerId);
        return true;
    }

    public boolean deleteVehicle(int vehicleId) {
        if (!vehicleRepo.get(vehicleId).isPresent()) {
            System.out.println("Vehicle not found.");
            return false;
        }
        if (vehicleHasBookings(vehicleId)) {
            System.out.println("Vehicle has bookings and cannot be deleted.");
            return false;
        }
        vehicleRepo.delete(vehicleId);
        return true;
    }

    public boolean deleteMechanic(int mechanicId) {
        if (!mechanicRepo.get(mechanicId).isPresent()) {
            System.out.println("Mechanic not found.");
            return false;
        }
        if (mechanicHasWorkOrders(mechanicId)) {
            System.out.println("Mechanic has work orders and cannot be deleted.");
            return false;
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

    public ServicePack updateServicePack(int servicePackId, ServicePack updatedServicePack){
        Optional<ServicePack> optionalServicePack = getServicePack(servicePackId);
        if(!optionalServicePack.isPresent()){
            throw new IllegalArgumentException("Service pack with ID " + servicePackId + " does not exist.");
        }

        //Om någon ServiceItem inte finns kasta exception
        updatedServicePack.getServiceItems().forEach(si -> {
            Optional<ServiceItem> optionalServiceItem = getServiceItem(si.getId());
            if(!optionalServiceItem.isPresent()) {
                throw new  IllegalArgumentException("Service item with ID " + si.getId() + " does not exist.");
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
            return new ArrayList<>();
        }

        return mechanics;
    }

    public List<Mechanic> getAvailableMechanics() {
        List<Mechanic> mechanics = mechanicRepo.getAllAvailable();
        if (mechanics.isEmpty()) {
            System.out.println("No available mechanics found.");
            return new ArrayList<>();
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
            return new ArrayList<>();
        }

        return workOrders;
    }

    public List<WorkOrder> getWorkOrdersWithItems(){
        List<WorkOrder> workOrders = workOrderRepo.getAllWithItems();
        if (workOrders.isEmpty()) {
            System.out.println("No work orders found.");
            return new ArrayList<>();
        }

        return workOrders;
    }

    public Optional<WorkOrder> getWorkOrder(int id){
        return workOrderRepo.get(id);
    }

    public Optional<WorkOrder> getWorkOrderWithItems(int workOrderId) {
        return workOrderRepo.getWithItems(workOrderId);
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
            return new ArrayList<>();
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
            return new ArrayList<>();
        }

        return payments;
    }

    public Optional<Payment> getPayment(int id){
        return paymentRepo.get(id);
    }

    public Customer createCustomer(String name, String phone, String email) {
        Customer customer = new Customer(name, phone, email);

        Customer savedCustomer = customerRepo.save(customer);

        System.out.println("Customer created successfully.");
        System.out.println(savedCustomer);

        return savedCustomer;
    }

    public Vehicle createVehicle(String registrationNumber,
                                 String brand,
                                 String model,
                                 int year,
                                 int customerId) {

        Optional<Customer> optionalCustomer = getCustomer(customerId);

        if (!optionalCustomer.isPresent()) {
            System.out.println("Customer with ID " + customerId + " does not exist.");
            return null;
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

        Optional<Vehicle> optionalVehicle = getVehicle(vehicleId);

        if (!optionalVehicle.isPresent()) {
            System.out.println("Vehicle with ID " + vehicleId + " does not exist.");
            return null;
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

        Optional<Vehicle> optionalVehicle = getVehicle(vehicleId);

        if (!optionalVehicle.isPresent()) {
            System.out.println("Vehicle with ID " + vehicleId + " does not exist.");
            return null;
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
            throw new IllegalArgumentException("Service pack with name \"" + name + "\" already exists.");
        }
        ServicePack servicePack = new ServicePack(name);

        for(int serviceItemId : serviceItemIds) {
            Optional<ServiceItem> optionalServiceItem = getServiceItem(serviceItemId);
            if(!optionalServiceItem.isPresent()) {
                throw new  IllegalArgumentException("Service item with ID " + serviceItemId + " does not exist.");
            }
            ServiceItem serviceItem = optionalServiceItem.get();

            servicePack.addServiceItem(serviceItem);
        }

        return servicePackRepo.save(servicePack);
    }
  
    public Booking createBookingFrom(int previousBookingId) {
        Optional<Booking> optionalPrevious = getBooking(previousBookingId);

        if (!optionalPrevious.isPresent()) {
            System.out.println("Booking with ID " + previousBookingId + " does not exist.");
            return null;
        }
        Booking previous = optionalPrevious.get();

        if (previous.getVehicle() == null) {
            System.out.println("Booking with ID " + previousBookingId + " has no vehicle.");
            return null;
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

    @Deprecated
    public Booking createBooking(int vehicleId,
                                 LocalDate date,
                                 String description,
                                 int mechanicId) {
        Booking booking = createBooking(vehicleId, date, description);
        //TODO should not be use
        /*if (booking != null) {

            booking.setMechanic(mechanicId);
        }*/
        return booking;
    }

    public WorkOrder createWorkOrder(int bookingId,
                                     int mechanicId,
                                     int... serviceItemIds)
    throws IllegalArgumentException{

        Optional<Booking> optionalBooking = getBooking(bookingId);

        if (!optionalBooking.isPresent()) {
            System.out.println("Booking with ID " + bookingId + " does not exist.");
            return null;
        }
        Booking booking = optionalBooking.get();

        //WorkOrder can in draft have no mechanic and no serviceItems, if mechanic or serviceItem is
        // selected but does not exist, the method throws IllegalArgumentException
        Mechanic mechanic = null;
        if(mechanicId > 0) {
            Optional<Mechanic> optionalMechanic = getMechanic(mechanicId);

            if (!optionalMechanic.isPresent()) {
                throw new IllegalArgumentException(String.format("There is no mechanic with id %d", mechanicId));
            }
            mechanic = optionalMechanic.get();

            if (!mechanic.isAvailable()) {
                throw new IllegalArgumentException("Mechanic " + mechanic.getName() + " is not available.");
            }
        }

        WorkOrder workOrder = new WorkOrder(
                booking,
                mechanic
        );

        for (int serviceItemId : serviceItemIds) {
            Optional<ServiceItem> optionalServiceItem = getServiceItem(serviceItemId);
            if (!optionalServiceItem.isPresent()) {
                throw new IllegalArgumentException(
                        "Service item with ID " + serviceItemId + " does not exist."
                );
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
            throw new IllegalArgumentException("Work order with ID " + workOrderId + " does not exist.");
        }

        WorkOrder workOrder = optionalWorkOrder.get();

        if (workOrder.getStatus() != WorkOrderStatus.DRAFT) {
            throw new IllegalArgumentException("Wrong status on work order. Only draft work orders can be confirmed.");
        }

        if(workOrder.getItems().isEmpty()){
            throw new IllegalArgumentException("No service items. Work order must contains at least one service item");
        }

        workOrder.setStatus(WorkOrderStatus.CONFIRMED);
        workOrderRepo.update(workOrder);

        System.out.println("Work order " + workOrderId + " has been confirmed.");
    }
    public WorkOrder updateWorkOrderServices(int workOrderId,
                                             List<Integer> serviceItemIdsToAdd,
                                             List<Integer> itemIdsToRemove)
    throws IllegalArgumentException{
        Optional<WorkOrder> optionalWorkOrder = getWorkOrderWithItems(workOrderId);

        if (!optionalWorkOrder.isPresent()) {
            throw new IllegalArgumentException("Work order with ID " + workOrderId + " does not exist.");
        }
        WorkOrder workOrder = optionalWorkOrder.get();

        if (!workOrder.getStatus().canEditServices()) {
            throw new IllegalArgumentException("Services can only be changed before the work order is started.");
        }

        for (Integer itemId : itemIdsToRemove) {
            if (!workOrder.removeItem(itemId)) {
                throw new IllegalArgumentException("Item with ID " + itemId + " is not on the work order.");
            }
        }

        for (Integer serviceItemId : serviceItemIdsToAdd) {
            Optional<ServiceItem> optionalServiceItem = getServiceItem(serviceItemId);

            if (!optionalServiceItem.isPresent()) {
                throw new IllegalArgumentException("Service item with ID " + serviceItemId + " does not exist.");
            }

            if (workOrder.hasService(serviceItemId)) {
                throw new IllegalArgumentException("Service is already on the work order.");

            }

            ServiceItem serviceItem = optionalServiceItem.get();
            workOrder.addItem(serviceItem, serviceItem.getPrice());
        }

        if (workOrder.getItems().isEmpty()) {
            throw new IllegalArgumentException("A work order must have at least one service.");
        }

        return workOrderRepo.update(workOrder);
    }

    public void startWorkOrder(int workOrderId) {
        Optional<WorkOrder> optionalWorkOrder = getWorkOrderWithItems(workOrderId);

        if (!optionalWorkOrder.isPresent()) {
            throw new IllegalArgumentException("Work order with ID " + workOrderId + " does not exist.");
        }
        WorkOrder workOrder = optionalWorkOrder.get();

        if (workOrder.getStatus() != WorkOrderStatus.CONFIRMED) {
            throw new IllegalArgumentException("Only confirmed work orders can be started.");
        }

        Mechanic mechanic = workOrder.getMechanic();

        if(mechanic == null){
            throw new IllegalArgumentException("No mechanic. Mechanic must be selected to confirm order");
        }

        mechanic.setAvailable(false);

        if(workOrder.getItems().isEmpty()){
            throw new IllegalArgumentException("No service items. Work order must contains at least one service item");
        }

        Booking booking = workOrder.getBooking();

        if (booking != null) {
            booking.setStatus("IN_PROGRESS");
        }

        workOrder.setStatus(WorkOrderStatus.IN_PROGRESS);
        workOrderRepo.update(workOrder);

        System.out.println("Work order " + workOrderId + " has been started.");
    }

    public void completeWorkOrder(int workOrderId) {
        Optional<WorkOrder> optionalWorkOrder = getWorkOrder(workOrderId);

        if (!optionalWorkOrder.isPresent()) {
            throw new IllegalArgumentException("Work order with ID " + workOrderId + " does not exist.");
        }
        WorkOrder workOrder = optionalWorkOrder.get();

        if (workOrder.getStatus() != WorkOrderStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Only work orders in progress can be completed.");

        }

        Mechanic mechanic = workOrder.getMechanic();
        Booking booking = workOrder.getBooking();

        if (mechanic != null) {
            mechanic.setAvailable(true);
        }

        if (booking != null) {
            booking.setStatus("COMPLETED");
        }

        workOrder.setStatus(WorkOrderStatus.COMPLETED);
        workOrderRepo.update(workOrder);

        System.out.println("Work order " + workOrderId + " has been completed.");
    }

    public void cancelWorkOrder(int workOrderId) {
        Optional<WorkOrder> optionalWorkOrder = getWorkOrder(workOrderId);

        if (!optionalWorkOrder.isPresent()) {
            throw new IllegalArgumentException("Work order with ID " + workOrderId + " does not exist.");
        }

        WorkOrder workOrder = optionalWorkOrder.get();

        if (!workOrder.getStatus().canBeCancelled()) {
            throw new IllegalArgumentException("Work order cannot be cancelled.");
        }

        Mechanic mechanic = workOrder.getMechanic();
        Booking booking = workOrder.getBooking();

        if (workOrder.getStatus() == WorkOrderStatus.IN_PROGRESS && mechanic != null) {
            mechanic.setAvailable(true);
        }

        if (booking != null) {
            booking.setStatus("CANCELLED");
        }

        workOrder.setStatus(WorkOrderStatus.CANCELLED);
        workOrderRepo.update(workOrder);

        System.out.println("Work order " + workOrderId + " has been cancelled.");
    }

    public Invoice createInvoice(int workOrderId, String discountCode) {
        Optional<WorkOrder> optionalWorkOrder = getWorkOrderWithItems(workOrderId);

        if (!optionalWorkOrder.isPresent()) {
            throw new IllegalArgumentException("Work order with ID " + workOrderId + " does not exist.");
        }
        WorkOrder workOrder = optionalWorkOrder.get();

        if (workOrder.getStatus() != WorkOrderStatus.COMPLETED) {
            throw new IllegalArgumentException("Invoice can only be created for a completed work order.");
        }

        double amount = 0.0;

        for (WorkOrderItem item : workOrder.getItems()) {
            amount += item.getPriceAtOrder();
        }

        double discount = 0.0;

        Booking booking = workOrder.getBooking();

        if (booking != null
                && booking.getVehicle() != null
                && booking.getVehicle().getCustomer() != null
                && booking.getVehicle().getCustomer().isVip()) {

            discount += amount * 0.10;
            System.out.println("VIP discount applied: 10%");
        }

        if (discountCode != null && !discountCode.trim().isEmpty()) {

            if (discountCode.equalsIgnoreCase("WELCOME10")) {
                discount += amount * 0.10;
                System.out.println("Discount code WELCOME10 applied.");

            } else if (discountCode.equalsIgnoreCase("SERVICE200")) {
                discount += 200.0;
                System.out.println("Discount code SERVICE200 applied.");

            } else {
                System.out.println("Unknown discount code. No code discount applied.");
            }
        }

        if (discount > amount) {
            discount = amount;
        }


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
            System.out.println("Invoice with ID " + invoiceId + " does not exist.");
            return null;
        }
        Invoice invoice = optionalInvoice.get();

        if (invoice.isPaid()) {
            System.out.println("Invoice has already been paid.");
            return null;
        }

        Payment payment = new Payment(
                invoice,
                invoice.getTotalAmount(),
                paymentType
        );

        boolean successful = false;

        if (paymentType.equalsIgnoreCase("CARD")) {

            System.out.println("Connecting directly to SuperCardPayment...");
            System.out.println("Card payment approved.");
            successful = true;

        } else if (paymentType.equalsIgnoreCase("SWISH")) {

            System.out.println("Calling Swish payment service...");
            System.out.println("Swish payment approved.");
            successful = true;

        } else if (paymentType.equalsIgnoreCase("CASH")) {

            System.out.println("Registering cash payment...");
            successful = true;

        } else {

            System.out.println("Unknown payment type.");
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