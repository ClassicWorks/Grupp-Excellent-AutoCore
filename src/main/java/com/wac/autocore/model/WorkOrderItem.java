package com.wac.autocore.model;

import javax.persistence.*;

@Entity
@Table(name = "work_order_items")
public class WorkOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @ManyToOne
    @JoinColumn(name = "service_item_id", nullable = false)
    private ServiceItem serviceItem;

    @Column(name = "price_at_order", nullable = false)
    private double priceAtOrder;

    public WorkOrderItem(WorkOrder workOrder, ServiceItem serviceItem, double priceAtOrder) {
        this.workOrder = workOrder;
        this.serviceItem = serviceItem;
        this.priceAtOrder = priceAtOrder;
    }

    protected WorkOrderItem(){

    }

    public int getId() {
        return id;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public ServiceItem getServiceItem() {
        return serviceItem;
    }

    public double getPriceAtOrder() {
        return priceAtOrder;
    }

    @Override
    public String toString() {
        return serviceItem.getName() + " | Price at order: " + priceAtOrder + " SEK";
    }
}
