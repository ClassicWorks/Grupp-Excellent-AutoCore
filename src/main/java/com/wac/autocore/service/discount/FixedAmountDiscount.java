package com.wac.autocore.service.discount;

public class FixedAmountDiscount implements Discount {
    private final double value;

    public FixedAmountDiscount(double value) {
        this.value = value;
    }

    @Override
    public double calculate(double amount) {
        return value;
    }
}
