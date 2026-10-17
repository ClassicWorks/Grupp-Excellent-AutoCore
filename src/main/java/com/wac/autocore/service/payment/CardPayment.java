package com.wac.autocore.service.payment;

public class CardPayment implements PaymentMethod {

    @Override
    public boolean pay(double amount) {
        System.out.println("Connecting directly to SuperCardPayment...");
        System.out.println("Card payment approved.");
        return true;
    }
}