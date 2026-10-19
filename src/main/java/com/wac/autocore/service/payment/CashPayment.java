package com.wac.autocore.service.payment;

public class CashPayment implements PaymentMethod {

    @Override
    public boolean pay(double amount) {
        System.out.println("Registering cash payment...");
        return true;
    }
}