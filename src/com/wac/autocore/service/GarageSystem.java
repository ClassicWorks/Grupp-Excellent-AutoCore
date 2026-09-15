package com.wac.autocore.service;

import com.wac.autocore.data.Database;
import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Invoice;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.Payment;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.model.WorkOrder;

import java.time.LocalDate;

public class GarageSystem {

    public void showCustomers() {
        System.out.println();
        System.out.println("=== CUSTOMERS ===");

        if (Database.getCustomers().isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        for (Customer customer : Database.getCustomers()) {
            System.out.println(customer);
        }
    }

    public void showVehicles() {
        System.out.println();
        System.out.println("=== VEHICLES ===");

        if (Database.getVehicles().isEmpty()) {
            System.out.println("No vehicles found.");
            return;
        }

        for (Vehicle vehicle : Database.getVehicles()) {
            System.out.println(vehicle);
        }
    }

    public void showBookings() {
        System.out.println();
        System.out.println("=== BOOKINGS ===");

        if (Database.getBookings().isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Booking booking : Database.getBookings()) {
            System.out.println(booking);
        }
    }

    public void showServiceItems() {
        System.out.println();
        System.out.println("=== SERVICES ===");

        if (Database.getServiceItems().isEmpty()) {
            System.out.println("No services found.");
            return;
        }

        for (ServiceItem serviceItem : Database.getServiceItems()) {
            System.out.println(serviceItem);
        }
    }

    public void showMechanics() {
        System.out.println();
        System.out.println("=== MECHANICS ===");

        if (Database.getMechanics().isEmpty()) {
            System.out.println("No mechanics found.");
            return;
        }

        for (Mechanic mechanic : Database.getMechanics()) {
            System.out.println(mechanic);
        }
    }

    public void showWorkOrders() {
        System.out.println();
        System.out.println("=== WORK ORDERS ===");

        if (Database.getWorkOrders().isEmpty()) {
            System.out.println("No work orders found.");
            return;
        }

        for (WorkOrder workOrder : Database.getWorkOrders()) {
            System.out.println(workOrder);
        }
    }

    public void showInvoices() {
        System.out.println();
        System.out.println("=== INVOICES ===");

        if (Database.getInvoices().isEmpty()) {
            System.out.println("No invoices found.");
            return;
        }

        for (Invoice invoice : Database.getInvoices()) {
            System.out.println(invoice);
        }
    }

    public void showPayments() {
        System.out.println();
        System.out.println("=== PAYMENTS ===");

        if (Database.getPayments().isEmpty()) {
            System.out.println("No payments found.");
            return;
        }

        for (Payment payment : Database.getPayments()) {
            System.out.println(payment);
        }
    }

    public Customer createCustomer(String name, String phone, String email) {
        int id = Database.getCustomers().size() + 1;

        Customer customer = new Customer(id, name, phone, email);
        Database.getCustomers().add(customer);

        System.out.println("Customer created successfully.");
        System.out.println(customer);

        return customer;
    }

    public Vehicle createVehicle(String registrationNumber,
                                 String brand,
                                 String model,
                                 int year,
                                 int customerId) {

        Customer customer = findCustomer(customerId);

        if (customer == null) {
            System.out.println("Customer with ID " + customerId + " does not exist.");
            return null;
        }

        int id = Database.getVehicles().size() + 1;

        Vehicle vehicle = new Vehicle(
                id,
                registrationNumber,
                brand,
                model,
                year,
                customerId
        );

        Database.getVehicles().add(vehicle);

        System.out.println("Vehicle created successfully.");
        System.out.println(vehicle);

        return vehicle;
    }

    public Booking createBooking(int vehicleId,
                                 LocalDate date,
                                 String description) {

        Vehicle vehicle = findVehicle(vehicleId);

        if (vehicle == null) {
            System.out.println("Vehicle with ID " + vehicleId + " does not exist.");
            return null;
        }

        int id = Database.getBookings().size() + 1;

        Booking booking = new Booking(
                id,
                vehicleId,
                date,
                description
        );

        Database.getBookings().add(booking);

        System.out.println("Booking created successfully.");
        System.out.println(booking);

        return booking;
    }

    public WorkOrder createWorkOrder(int bookingId,
                                     int mechanicId,
                                     int... serviceItemIds) {

        Booking booking = findBooking(bookingId);

        if (booking == null) {
            System.out.println("Booking with ID " + bookingId + " does not exist.");
            return null;
        }

        Mechanic mechanic = findMechanic(mechanicId);

        if (mechanic == null) {
            System.out.println("Mechanic with ID " + mechanicId + " does not exist.");
            return null;
        }

        if (!mechanic.isAvailable()) {
            System.out.println("Mechanic " + mechanic.getName() + " is not available.");
            return null;
        }

        for (int serviceItemId : serviceItemIds) {
            if (findServiceItem(serviceItemId) == null) {
                System.out.println(
                        "Service item with ID " + serviceItemId + " does not exist."
                );
                return null;
            }
        }

        int id = Database.getWorkOrders().size() + 1;

        WorkOrder workOrder = new WorkOrder(
                id,
                bookingId,
                mechanicId
        );

        for (int serviceItemId : serviceItemIds) {
            workOrder.addServiceItem(serviceItemId);
        }

        Database.getWorkOrders().add(workOrder);

        booking.setStatus("WORK_ORDER_CREATED");

        System.out.println("Work order created successfully.");
        System.out.println(workOrder);

        return workOrder;
    }

    public void startWorkOrder(int workOrderId) {
        WorkOrder workOrder = findWorkOrder(workOrderId);

        if (workOrder == null) {
            System.out.println("Work order with ID " + workOrderId + " does not exist.");
            return;
        }

        if (!workOrder.getStatus().equals("CREATED")) {
            System.out.println("Work order cannot be started.");
            return;
        }

        Mechanic mechanic = findMechanic(workOrder.getMechanicId());
        Booking booking = findBooking(workOrder.getBookingId());

        if (mechanic != null) {
            mechanic.setAvailable(false);
        }

        if (booking != null) {
            booking.setStatus("IN_PROGRESS");
        }

        workOrder.setStatus("IN_PROGRESS");

        System.out.println("Work order " + workOrderId + " has been started.");
    }

    public void completeWorkOrder(int workOrderId) {
        WorkOrder workOrder = findWorkOrder(workOrderId);

        if (workOrder == null) {
            System.out.println("Work order with ID " + workOrderId + " does not exist.");
            return;
        }

        if (!workOrder.getStatus().equals("IN_PROGRESS")) {
            System.out.println("Only work orders in progress can be completed.");
            return;
        }

        Mechanic mechanic = findMechanic(workOrder.getMechanicId());
        Booking booking = findBooking(workOrder.getBookingId());

        workOrder.setStatus("COMPLETED");

        if (mechanic != null) {
            mechanic.setAvailable(true);
        }

        if (booking != null) {
            booking.setStatus("COMPLETED");
        }

        System.out.println("Work order " + workOrderId + " has been completed.");
    }

    public Invoice createInvoice(int workOrderId, String discountCode) {
        WorkOrder workOrder = findWorkOrder(workOrderId);

        if (workOrder == null) {
            System.out.println("Work order with ID " + workOrderId + " does not exist.");
            return null;
        }

        if (!workOrder.getStatus().equals("COMPLETED")) {
            System.out.println("Invoice can only be created for a completed work order.");
            return null;
        }

        double amount = 0.0;

        for (Integer serviceItemId : workOrder.getServiceItemIds()) {
            ServiceItem serviceItem = findServiceItem(serviceItemId);

            if (serviceItem != null) {
                amount += serviceItem.getPrice();
            }
        }

        double discount = 0.0;

        Booking booking = findBooking(workOrder.getBookingId());

        if (booking != null) {
            Vehicle vehicle = findVehicle(booking.getVehicleId());

            if (vehicle != null) {
                Customer customer = findCustomer(vehicle.getCustomerId());

                if (customer != null && customer.isVip()) {
                    discount += amount * 0.10;
                    System.out.println("VIP discount applied: 10%");
                }
            }
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

        int id = Database.getInvoices().size() + 1;

        Invoice invoice = new Invoice(
                id,
                workOrderId,
                LocalDate.now(),
                amount
        );

        invoice.setDiscount(discount);

        Database.getInvoices().add(invoice);

        System.out.println("Invoice created successfully.");
        System.out.println(invoice);

        System.out.println("Sending invoice notification to customer...");
        System.out.println("Notification sent.");

        return invoice;
    }

    public Payment processPayment(int invoiceId, String paymentType) {
        Invoice invoice = findInvoice(invoiceId);

        if (invoice == null) {
            System.out.println("Invoice with ID " + invoiceId + " does not exist.");
            return null;
        }

        if (invoice.isPaid()) {
            System.out.println("Invoice has already been paid.");
            return null;
        }

        int id = Database.getPayments().size() + 1;

        Payment payment = new Payment(
                id,
                invoiceId,
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
        Database.getPayments().add(payment);

        if (successful) {
            invoice.setPaid(true);

            System.out.println("Payment completed successfully.");
            System.out.println("Sending payment confirmation to customer...");
            System.out.println("Confirmation sent.");
        } else {
            System.out.println("Payment failed.");
        }

        return payment;
    }

    private Customer findCustomer(int id) {
        for (Customer customer : Database.getCustomers()) {
            if (customer.getId() == id) {
                return customer;
            }
        }

        return null;
    }

    private Vehicle findVehicle(int id) {
        for (Vehicle vehicle : Database.getVehicles()) {
            if (vehicle.getId() == id) {
                return vehicle;
            }
        }

        return null;
    }

    private Booking findBooking(int id) {
        for (Booking booking : Database.getBookings()) {
            if (booking.getId() == id) {
                return booking;
            }
        }

        return null;
    }

    private Mechanic findMechanic(int id) {
        for (Mechanic mechanic : Database.getMechanics()) {
            if (mechanic.getId() == id) {
                return mechanic;
            }
        }

        return null;
    }

    private ServiceItem findServiceItem(int id) {
        for (ServiceItem serviceItem : Database.getServiceItems()) {
            if (serviceItem.getId() == id) {
                return serviceItem;
            }
        }

        return null;
    }

    private WorkOrder findWorkOrder(int id) {
        for (WorkOrder workOrder : Database.getWorkOrders()) {
            if (workOrder.getId() == id) {
                return workOrder;
            }
        }

        return null;
    }

    private Invoice findInvoice(int id) {
        for (Invoice invoice : Database.getInvoices()) {
            if (invoice.getId() == id) {
                return invoice;
            }
        }

        return null;
    }
}