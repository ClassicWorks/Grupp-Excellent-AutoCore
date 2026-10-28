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

    public PaymentController(PaymentService paymentService, AccessGuard accessGuard) {
        this.paymentService = paymentService;
        this.accessGuard = accessGuard;
    }

    public List<Payment> getPayments() {
        accessGuard.require(Permission.READ, "getPayments");
        return paymentService.getPayments();
    }

    public Optional<Payment> getPayment(int id) {
        accessGuard.require(Permission.READ, "getPayment");
        return paymentService.getPayment(id);
    }

    public Payment processPayment(int invoiceId, String paymentType) {
        accessGuard.require(Permission.MANAGE_PAYMENTS, "processPayment");
        return paymentService.processPayment(invoiceId, paymentType);
    }
}