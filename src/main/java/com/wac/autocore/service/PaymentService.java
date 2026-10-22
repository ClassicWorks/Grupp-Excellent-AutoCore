package com.wac.autocore.service;

import com.wac.autocore.data.PaymentRepo;
import com.wac.autocore.exceptions.InputNotValidException;
import com.wac.autocore.exceptions.InvoiceNotFoundException;
import com.wac.autocore.exceptions.StatusNotValidException;
import com.wac.autocore.model.Invoice;
import com.wac.autocore.model.Payment;
import com.wac.autocore.service.payment.PaymentMethod;
import com.wac.autocore.service.payment.PaymentMethodFactory;

import java.util.List;
import java.util.Optional;

public class PaymentService {
    private final PaymentRepo paymentRepo;
    private final InvoiceService invoiceService;

    public PaymentService(PaymentRepo paymentRepo, InvoiceService invoiceService) {
        this.paymentRepo = paymentRepo;
        this.invoiceService = invoiceService;
    }

    public List<Payment> getPayments() {
        List<Payment> payments = paymentRepo.getAll();
        if (payments.isEmpty()) {
            System.out.println("No payments found.");
        }
        return payments;
    }

    public Optional<Payment> getPayment(int id) {
        return paymentRepo.get(id);
    }

    public Payment processPayment(int invoiceId, String paymentType) {
        Invoice invoice = invoiceService.getInvoice(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException(invoiceId));

        if (invoice.isPaid()) {
            throw new StatusNotValidException("Invoice has already been paid.");
        }

        boolean successful = executePayment(paymentType, invoice.getTotalAmount());

        Payment payment = new Payment(
                invoice,
                invoice.getTotalAmount(),
                paymentType
        );
        payment.setSuccessful(successful);
        Payment savedPayment = paymentRepo.save(payment);

        if (successful) {
            invoiceService.markAsPaid(invoice);

            System.out.println("Payment completed successfully.");
            System.out.println("Sending payment confirmation to customer...");
            System.out.println("Confirmation sent.");
        } else {
            System.out.println("Payment failed.");
        }

        return savedPayment;
    }

    private boolean executePayment(String paymentType, double amount) {
        Optional<PaymentMethod> paymentMethod = PaymentMethodFactory.create(paymentType);

        if (!paymentMethod.isPresent()) {
            throw new InputNotValidException("Unknown payment type: " + paymentType);
        }

        return paymentMethod.get().pay(amount);
    }
}