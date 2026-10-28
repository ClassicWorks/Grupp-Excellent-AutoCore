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
    private final ErrorHandler errorHandler;

    public InvoiceController(InvoiceService invoiceService, AccessGuard accessGuard, ErrorHandler errorHandler) {
        this.invoiceService = invoiceService;
        this.accessGuard = accessGuard;
        this.errorHandler = errorHandler;
    }

    public Result<List<Invoice>> getInvoices() {
        return errorHandler.call(() -> {
            accessGuard.require(Permission.READ, "getInvoices");
            return invoiceService.getInvoices();
        });
    }

    public Result<Optional<Invoice>> getInvoice(int id) {
        return errorHandler.call(() -> {
            accessGuard.require(Permission.READ, "getInvoice");
            return invoiceService.getInvoice(id);
        });
    }

    public Result<List<Invoice>> getUnpaidInvoices() {
        return errorHandler.call(() -> {
            accessGuard.require(Permission.READ, "getUnpaidInvoices");
            return invoiceService.getUnpaidInvoices();
        });
    }

    public Result<List<Invoice>> getPaidInvoices() {
        return errorHandler.call(() -> {
            accessGuard.require(Permission.READ, "getPaidInvoices");
            return invoiceService.getPaidInvoices();
        });
    }

    public Result<List<WorkOrder>> getWorkOrdersThatCanBeInvoiced() {
        return errorHandler.call(() -> {
            accessGuard.require(Permission.READ, "getWorkOrdersThatCanBeInvoiced");
            return invoiceService.getWorkOrdersThatCanBeInvoiced();
        });
    }

    public Result<Invoice> createInvoice(int workOrderId, String discountCode) {
        return errorHandler.call(() -> {
            accessGuard.require(Permission.MANAGE_INVOICES, "createInvoice");
            return invoiceService.createInvoice(workOrderId, discountCode);
        });
    }
}