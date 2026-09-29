package com.wac.autocore.model;

import javax.persistence.*;
import java.time.LocalDate;
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "work_order_id")
    private WorkOrder workOrder;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "amount")
    private double amount;

    @Column(name = "discount")
    private double discount;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "paid")
    private boolean paid;

    public Invoice(int id, WorkOrder workOrder, LocalDate invoiceDate, double amount) {
        this.id = id;
        this.workOrder = workOrder;
        this.invoiceDate = invoiceDate;
        this.amount = amount;
        this.discount = 0.0;
        this.totalAmount = amount;
        this.paid = false;
    }

    public Invoice(WorkOrder workOrder, LocalDate invoiceDate, double amount) {
        this.workOrder = workOrder;
        this.invoiceDate = invoiceDate;
        this.amount = amount;
        this.discount = 0.0;
        this.totalAmount = amount;
        this.paid = false;
    }

    protected Invoice() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
        calculateTotalAmount();
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
        calculateTotalAmount();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    private void calculateTotalAmount() {
        this.totalAmount = amount - discount;
    }

    @Override
    public String toString() {
        return id +
                " - Work order ID: " + workOrder +
                " | Date: " + invoiceDate +
                " | Amount: " + amount + " SEK" +
                " | Discount: " + discount + " SEK" +
                " | Total: " + totalAmount + " SEK" +
                " | Paid: " + (paid ? "Yes" : "No");
    }
}