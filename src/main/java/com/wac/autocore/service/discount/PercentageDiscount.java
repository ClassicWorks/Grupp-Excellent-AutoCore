package com.wac.autocore.service.discount;

public class PercentageDiscount implements Discount {
    private final double rate;

    public PercentageDiscount(double rate) {
        this.rate = rate;
    }

    @Override
    public double calculate(double amount) {
        return amount * rate;
    }
}
