package com.wac.autocore.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "work_orders")
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "mechanic_id")
    private Mechanic mechanic;

    @ManyToMany
    @JoinTable(
            name = "join_work_order_service_item",
            joinColumns = @JoinColumn(name = "work_order_id"),
            inverseJoinColumns = @JoinColumn(name = "service_item_id")
    )
    private List<ServiceItem> serviceItems;

    @Column(name = "status", nullable = false, length = 15)
    private String status;

    /*public WorkOrder(int id, int bookingId, int Mechanic) {
        this.id = id;
        this.bookingId = bookingId;
        this.Mechanic = Mechanic;
        this.serviceItemIds = new ArrayList<Integer>();
        this.status = "CREATED";
    }*/

    public WorkOrder(Booking booking, Mechanic mechanic) {
        this.booking = booking;
        this.mechanic = mechanic;
        this.serviceItems = new ArrayList<>();
        this.status = "CREATED";
    }

    protected WorkOrder() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Mechanic getMechanic() {
        return mechanic;
    }

    public void setMechanic(Mechanic mechanic) {
        this.mechanic = mechanic;
    }

    public List<ServiceItem> getServiceItems() {
        return serviceItems;
    }

    public void setServiceItems(List<ServiceItem> serviceItems) {
        this.serviceItems = serviceItems;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addServiceItem(ServiceItem serviceItem) {
        serviceItems.add(serviceItem);
    }

    public void removeServiceItem(ServiceItem serviceItem) {
        serviceItems.remove(serviceItem);
    }

    @Override
    public String toString() {
        return id +
                " - Booking ID: " + booking +
                " | Mechanic ID: " + mechanic +
                //" | Services: " + getServiceItems() +
                " | Status: " + status;
    }
}