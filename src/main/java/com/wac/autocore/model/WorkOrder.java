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

    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkOrderItem> items = new ArrayList<>();

    @Column(name = "status", nullable = false, length = 15)
    private String status;

    public WorkOrder(Booking booking, Mechanic mechanic) {
        this.booking = booking;
        this.mechanic = mechanic;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addItem(ServiceItem serviceItem, double priceAtOrder) {
        items.add(new WorkOrderItem(this, serviceItem, priceAtOrder));
    }

    public boolean removeItem(int itemId) {
        return items.removeIf(item -> item.getId() == itemId);
    }

    public boolean hasService(int serviceItemId) {
        return items.stream()
                .anyMatch(item -> item.getServiceItem().getId() == serviceItemId);
    }

    public List<WorkOrderItem> getItems() {
        return items;
    }

    public double getTotalPrice() {
        double total = 0;
        for (WorkOrderItem item : items) {
            total += item.getPriceAtOrder();
        }
        return total;
    }

    @Override
    public String toString() {
        return id +
                " - Booking ID: " + booking +
                " | Mechanic ID: " + mechanic +
                " | Status: " + status;
    }
}