package com.wac.autocore.data;

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

public class Database {

    private static final List<Customer> customers = new ArrayList<Customer>();
    private static final List<Vehicle> vehicles = new ArrayList<Vehicle>();
    private static final List<Booking> bookings = new ArrayList<Booking>();
    private static final List<ServiceItem> serviceItems = new ArrayList<ServiceItem>();
    private static final List<Mechanic> mechanics = new ArrayList<Mechanic>();
    private static final List<WorkOrder> workOrders = new ArrayList<WorkOrder>();
    private static final List<Invoice> invoices = new ArrayList<Invoice>();
    private static final List<Payment> payments = new ArrayList<Payment>();

    static {
        loadSampleData();
    }

    private static void loadSampleData() {

        customers.add(new Customer(
                1,
                "Anna Andersson",
                "070-1111111",
                "anna.andersson@email.se"
        ));

        customers.add(new Customer(
                2,
                "Erik Eriksson",
                "070-2222222",
                "erik.eriksson@email.se"
        ));

        customers.add(new Customer(
                3,
                "Maria Svensson",
                "070-3333333",
                "maria.svensson@email.se"
        ));

        customers.get(1).setVip(true);

        vehicles.add(new Vehicle(
                1,
                "ABC123",
                "Volvo",
                "V70",
                2012,
                1
        ));

        vehicles.add(new Vehicle(
                2,
                "DEF456",
                "Volkswagen",
                "Passat",
                2018,
                2
        ));

        vehicles.add(new Vehicle(
                3,
                "GHI789",
                "Toyota",
                "Corolla",
                2020,
                3
        ));

        serviceItems.add(new ServiceItem(
                1,
                "Oil change",
                "Engine oil and oil filter replacement",
                1295.0,
                45
        ));

        serviceItems.add(new ServiceItem(
                2,
                "Brake service",
                "Inspection and replacement of front brake pads",
                2495.0,
                90
        ));

        serviceItems.add(new ServiceItem(
                3,
                "Diagnostics",
                "Electronic fault code diagnostics",
                995.0,
                60
        ));

        serviceItems.add(new ServiceItem(
                4,
                "Annual service",
                "Standard annual vehicle service",
                3495.0,
                120
        ));

        mechanics.add(new Mechanic(
                1,
                "Johan Karlsson",
                "070-5551111",
                "General service"
        ));

        mechanics.add(new Mechanic(
                2,
                "Sara Nilsson",
                "070-5552222",
                "Brakes"
        ));

        mechanics.add(new Mechanic(
                3,
                "Mikael Berg",
                "070-5553333",
                "Diagnostics"
        ));

        bookings.add(new Booking(
                1,
                1,
                LocalDate.now().plusDays(2),
                "Annual service and general inspection"
        ));

        bookings.add(new Booking(
                2,
                2,
                LocalDate.now().plusDays(4),
                "Noise from front brakes"
        ));
    }

    public static List<Customer> getCustomers() {
        return customers;
    }

    public static List<Vehicle> getVehicles() {
        return vehicles;
    }

    public static List<Booking> getBookings() {
        return bookings;
    }

    public static List<ServiceItem> getServiceItems() {
        return serviceItems;
    }

    public static List<Mechanic> getMechanics() {
        return mechanics;
    }

    public static List<WorkOrder> getWorkOrders() {
        return workOrders;
    }

    public static List<Invoice> getInvoices() {
        return invoices;
    }

    public static List<Payment> getPayments() {
        return payments;
    }
}