package com.wac.autocore.service;

import com.wac.autocore.data.*;
import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Invoice;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.Payment;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.model.WorkOrder;

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

    public GarageSystem() {
        vehicleRepo = new VehicleRepoImpl();
        customerRepo = new CustomerRepoImpl();
        mechanicRepo = new MechanicRepoImpl();
        serviceItemRepo = new ServiceItemRepoImpl();
        bookingRepo = new BookingRepoImpl();
        workOrderRepo = new WorkOrderRepoImpl();
        invoiceRepo = new InvoiceRepoImpl();
        paymentRepo  = new PaymentRepoImpl();
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

    public Optional<WorkOrder> getWorkOrder(int id){
        return workOrderRepo.get(id);
    }

    private Optional<WorkOrder> getWorkOrderWithServiceItems(int workOrderId) {
        return workOrderRepo.getWithServiceItems(workOrderId);
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
                                     int... serviceItemIds) {

        Optional<Booking> optionalBooking = getBooking(bookingId);

        if (!optionalBooking.isPresent()) {
            System.out.println("Booking with ID " + bookingId + " does not exist.");
            return null;
        }
        Booking booking = optionalBooking.get();

        Optional<Mechanic> optionalMechanic = getMechanic(mechanicId);

        if (!optionalMechanic.isPresent()) {
            System.out.println("Mechanic with ID " + mechanicId + " does not exist.");
            return null;
        }
        Mechanic mechanic = optionalMechanic.get();

        if (!mechanic.isAvailable()) {
            System.out.println("Mechanic " + mechanic.getName() + " is not available.");
            return null;
        }

        WorkOrder workOrder = new WorkOrder(
                booking,
                mechanic
        );

        for (int serviceItemId : serviceItemIds) {
            Optional<ServiceItem> optionalServiceItem = getServiceItem(serviceItemId);
            if (!optionalServiceItem.isPresent()) {
                System.out.println(
                        "Service item with ID " + serviceItemId + " does not exist."
                );
                return null;
            }
            workOrder.addServiceItem(optionalServiceItem.get());
        }


        WorkOrder savedWorkOrder = workOrderRepo.save(workOrder);
        booking.setStatus("WORK_ORDER_CREATED");
        bookingRepo.update(booking);

        System.out.println("Work order created successfully.");
        System.out.println(savedWorkOrder);

        return savedWorkOrder;
    }

    public void startWorkOrder(int workOrderId) {
        Optional<WorkOrder> optionalWorkOrder = getWorkOrder(workOrderId);

        if (!optionalWorkOrder.isPresent()) {
            System.out.println("Work order with ID " + workOrderId + " does not exist.");
            return;
        }
        WorkOrder workOrder = optionalWorkOrder.get();

        if (!workOrder.getStatus().equals("CREATED")) {
            System.out.println("Work order cannot be started.");
            return;
        }

        Mechanic mechanic = workOrder.getMechanic();
        Booking booking = workOrder.getBooking();

        if (mechanic != null) {
            mechanic.setAvailable(false);
        }

        if (booking != null) {
            booking.setStatus("IN_PROGRESS");
        }

        workOrder.setStatus("IN_PROGRESS");
        workOrderRepo.update(workOrder);

        System.out.println("Work order " + workOrderId + " has been started.");
    }

    public void completeWorkOrder(int workOrderId) {
        Optional<WorkOrder> optionalWorkOrder = getWorkOrder(workOrderId);

        if (!optionalWorkOrder.isPresent()) {
            System.out.println("Work order with ID " + workOrderId + " does not exist.");
            return;
        }
        WorkOrder workOrder = optionalWorkOrder.get();

        if (!workOrder.getStatus().equals("IN_PROGRESS")) {
            System.out.println("Only work orders in progress can be completed.");
            return;
        }

        Mechanic mechanic = workOrder.getMechanic();
        Booking booking = workOrder.getBooking();

        if (mechanic != null) {
            mechanic.setAvailable(true);
        }

        if (booking != null) {
            booking.setStatus("COMPLETED");
        }

        workOrder.setStatus("COMPLETED");
        workOrderRepo.update(workOrder);

        System.out.println("Work order " + workOrderId + " has been completed.");
    }

    public Invoice createInvoice(int workOrderId, String discountCode) {
        Optional<WorkOrder> optionalWorkOrder = getWorkOrderWithServiceItems(workOrderId);

        if (!optionalWorkOrder.isPresent()) {
            System.out.println("Work order with ID " + workOrderId + " does not exist.");
            return null;
        }
        WorkOrder workOrder = optionalWorkOrder.get();

        if (!workOrder.getStatus().equals("COMPLETED")) {
            System.out.println("Invoice can only be created for a completed work order.");
            return null;
        }

        double amount = 0.0;

        for (ServiceItem serviceItem : workOrder.getServiceItems()) {
                amount += serviceItem.getPrice();
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