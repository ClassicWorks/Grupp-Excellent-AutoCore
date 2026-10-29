package com.wac.autocore.controller;

public record AppControllers(
        WorkOrderController workOrders,
        InvoiceController invoices,
        PaymentController payments,
        CustomerController customers,
        CatalogController catalog) {
}
