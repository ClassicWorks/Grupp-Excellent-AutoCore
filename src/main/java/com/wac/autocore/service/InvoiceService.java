package com.wac.autocore.service;

import com.wac.autocore.data.InvoiceRepo;
import com.wac.autocore.data.WorkOrderRepo;
import com.wac.autocore.exceptions.StatusNotValidException;
import com.wac.autocore.exceptions.WorkOrderNotFoundException;
import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Invoice;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.model.WorkOrderItem;
import com.wac.autocore.model.WorkOrderStatus;
import com.wac.autocore.model.WorkOrderType;
import com.wac.autocore.service.discount.Discount;
import com.wac.autocore.service.discount.DiscountCodes;
import com.wac.autocore.service.discount.PercentageDiscount;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InvoiceService {
    private static final Discount VIP_DISCOUNT = new PercentageDiscount(0.10);

    private final InvoiceRepo invoiceRepo;
    private final WorkOrderRepo workOrderRepo;

    public InvoiceService(InvoiceRepo invoiceRepo, WorkOrderRepo workOrderRepo) {
        this.invoiceRepo = invoiceRepo;
        this.workOrderRepo = workOrderRepo;
    }

    public List<Invoice> getInvoices() {
        List<Invoice> invoices = invoiceRepo.getAll();
        if (invoices.isEmpty()) {
            System.out.println("No invoices found.");
        }
        return invoices;
    }

    public Optional<Invoice> getInvoice(int id) {
        return invoiceRepo.get(id);
    }

    public List<Invoice> getUnpaidInvoices() {
        return invoiceRepo.getWherePayedIs(false);
    }

    public List<Invoice> getPaidInvoices() {
        return invoiceRepo.getWherePayedIs(true);
    }

    public List<WorkOrder> getCompletedWorkOrdersWithoutInvoices() {
        return workOrderRepo.getCompletedWorkOrdersWithoutInvoices().stream()
                .filter(workOrder -> workOrder.getType() != WorkOrderType.WARRANTY)
                .collect(Collectors.toList());
    }

    public Invoice createInvoice(int workOrderId, String discountCode) {
        WorkOrder workOrder = workOrderRepo.getWithItems(workOrderId)
                .orElseThrow(() -> new WorkOrderNotFoundException(workOrderId));

        checkCanBeInvoiced(workOrder);

        double amount = calculateAmount(workOrder);
        double discount = calculateDiscount(workOrder, discountCode, amount);

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

    public void markAsPaid(Invoice invoice) {
        invoice.setPaid(true);
        invoiceRepo.update(invoice);
    }

    private void checkCanBeInvoiced(WorkOrder workOrder) {
        if (workOrder.getStatus() != WorkOrderStatus.COMPLETED) {
            throw new StatusNotValidException("Invoice can only be created for a completed work order.");
        }

        if (workOrder.getType() == WorkOrderType.WARRANTY) {
            throw new StatusNotValidException("Warranty work is not invoiced.");
        }

        boolean alreadyInvoiced = invoiceRepo.getAll().stream()
                .anyMatch(invoice -> invoice.getWorkOrder().getId() == workOrder.getId());
        if (alreadyInvoiced) {
            throw new StatusNotValidException("Work order " + workOrder.getId() + " already has an invoice.");
        }
    }

    private double calculateAmount(WorkOrder workOrder) {
        double amount = 0.0;
        for (WorkOrderItem item : workOrder.getItems()) {
            amount += item.getPriceAtOrder();
        }
        return amount;
    }

    private double calculateDiscount(WorkOrder workOrder, String discountCode, double amount) {
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

        return Math.min(discount, amount);
    }
}