package com.wac.autocore.controller;

import com.wac.autocore.model.Payment;
import com.wac.autocore.security.AccessGuard;
import com.wac.autocore.security.Permission;
import com.wac.autocore.service.PaymentService;

import java.util.List;
import java.util.Optional;

public class PaymentController {

    private final PaymentService paymentService;
    private final AccessGuard accessGuard;
    private final ErrorHandler errorHandler;

    public PaymentController(PaymentService paymentService, AccessGuard accessGuard, ErrorHandler errorHandler) {
        this.paymentService = paymentService;
        this.accessGuard = accessGuard;
        this.errorHandler = errorHandler;
    }

    public Result<List<Payment>> getPayments() {
        return errorHandler.call(() -> {
            accessGuard.require(Permission.READ, "getPayments");
            return paymentService.getPayments();
        });
    }

    public Result<Optional<Payment>> getPayment(int id) {
        return errorHandler.call(() -> {
            accessGuard.require(Permission.READ, "getPayment");
            return paymentService.getPayment(id);
        });
    }

    public Result<Payment> processPayment(int invoiceId, String paymentType) {
        return errorHandler.call(() -> {
            accessGuard.require(Permission.MANAGE_PAYMENTS, "processPayment");
            return paymentService.processPayment(invoiceId, paymentType);
        });
    }
}