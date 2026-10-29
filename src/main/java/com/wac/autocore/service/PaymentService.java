package com.wac.autocore.service;

import com.wac.autocore.data.PaymentRepo;
import com.wac.autocore.exceptions.InputNotValidException;
import com.wac.autocore.exceptions.InvoiceNotFoundException;
import com.wac.autocore.exceptions.StatusNotValidException;
import com.wac.autocore.model.Invoice;
import com.wac.autocore.model.Payment;
import com.wac.autocore.security.AccessGuard;
import com.wac.autocore.security.Permission;
import com.wac.autocore.service.payment.PaymentMethod;
import com.wac.autocore.service.payment.PaymentMethodFactory;

import java.util.List;
import java.util.Optional;

public class PaymentService {
    private final PaymentRepo paymentRepo;
    private final InvoiceService invoiceService;
    private final AccessGuard accessGuard;

    public PaymentService(PaymentRepo paymentRepo, InvoiceService invoiceService, AccessGuard accessGuard) {
        this.paymentRepo = paymentRepo;
        this.invoiceService = invoiceService;
        this.accessGuard = accessGuard;
    }

    public List<Payment> getPayments() {
        accessGuard.require(Permission.READ, "getPayments");
        List<Payment> payments = paymentRepo.getAll();
        if (payments.isEmpty()) {
            System.out.println("No payments found.");
        }
        return payments;
    }

    public Optional<Payment> getPayment(int id) {
        accessGuard.require(Permission.READ, "getPayment");
        return paymentRepo.get(id);
    }

    /**
     * Processes a payment for a specified invoice by the given payment type.
     * If the invoice has already been paid or an error occurs during the payment process,
     * appropriate exceptions are thrown. When payment is successful, the invoice is marked as paid
     * and a payment confirmation is sent.
     *
     * @param invoiceId    the unique identifier of the invoice to be paid
     * @param paymentType  the type of payment method to process the payment (e.g., credit card, bank transfer)
     * @return the saved Payment object containing the details of the processed payment
     * @throws InvoiceNotFoundException if no invoice is found with the specified invoiceId
     * @throws StatusNotValidException  if the invoice is already marked as paid
     * @throws InputNotValidException   if the provided paymentType is invalid or unsupported
     */
    public Payment processPayment(int invoiceId, String paymentType) {
        accessGuard.require(Permission.MANAGE_PAYMENTS, "processPayment");

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

    /**
     * Executes a payment using the specified payment type and amount.
     *
     * @param paymentType the type of payment method to process the payment (e.g., CARD, SWISH, CASH)
     * @param amount      the amount to be paid
     * @return true if the payment is successful, false otherwise
     * @throws InputNotValidException if the provided paymentType is invalid or unsupported
     */
    private boolean executePayment(String paymentType, double amount) {
        Optional<PaymentMethod> paymentMethod = PaymentMethodFactory.create(paymentType);

        if (!paymentMethod.isPresent()) {
            throw new InputNotValidException("Unknown payment type: " + paymentType);
        }

        return paymentMethod.get().pay(amount);
    }
}