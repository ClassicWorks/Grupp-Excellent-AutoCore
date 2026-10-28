package com.wac.autocore.controller;

import com.wac.autocore.model.Invoice;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.security.AccessGuard;
import com.wac.autocore.security.Permission;
import com.wac.autocore.service.InvoiceService;

import java.util.List;
import java.util.Optional;

public class InvoiceController {

    private final InvoiceService invoiceService;
    private final AccessGuard accessGuard;

    public InvoiceController(InvoiceService invoiceService, AccessGuard accessGuard) {
        this.invoiceService = invoiceService;
        this.accessGuard = accessGuard;
    }

    public List<Invoice> getInvoices() {
        accessGuard.require(Permission.READ, "getInvoices");
        return invoiceService.getInvoices();
    }

    public Optional<Invoice> getInvoice(int id) {
        accessGuard.require(Permission.READ, "getInvoice");
        return invoiceService.getInvoice(id);
    }

    public List<Invoice> getUnpaidInvoices() {
        accessGuard.require(Permission.READ, "getUnpaidInvoices");
        return invoiceService.getUnpaidInvoices();
    }

    public List<Invoice> getPaidInvoices() {
        accessGuard.require(Permission.READ, "getPaidInvoices");
        return invoiceService.getPaidInvoices();
    }

    public List<WorkOrder> getWorkOrdersThatCanBeInvoiced() {
        accessGuard.require(Permission.READ, "getWorkOrdersThatCanBeInvoiced");
        return invoiceService.getWorkOrdersThatCanBeInvoiced();
    }

    public Invoice createInvoice(int workOrderId, String discountCode) {
        accessGuard.require(Permission.MANAGE_INVOICES, "createInvoice");
        return invoiceService.createInvoice(workOrderId, discountCode);
    }
}