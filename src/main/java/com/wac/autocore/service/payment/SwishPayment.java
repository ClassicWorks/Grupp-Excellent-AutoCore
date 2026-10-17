package com.wac.autocore.service.payment;

public class SwishPayment implements PaymentMethod {

    @Override
    public boolean pay(double amount) {
        System.out.println("Calling Swish payment service...");
        System.out.println("Swish payment approved.");
        return true;
    }
}