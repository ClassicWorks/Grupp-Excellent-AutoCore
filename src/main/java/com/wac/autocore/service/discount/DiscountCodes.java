package com.wac.autocore.service.discount;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class DiscountCodes {
    private static final Map<String, Discount> CODES = new HashMap<>();

    static {
        CODES.put("WELCOME10", new PercentageDiscount(0.10));
        CODES.put("SERVICE200", new FixedAmountDiscount(200.0));
    }

    public static Optional<Discount> find(String code) {
        if (code == null || code.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(CODES.get(code.trim().toUpperCase(Locale.ROOT)));
    }
}
