package com.wac.autocore.service.payment;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class PaymentMethodFactory {

    private static final Map<String, Supplier<PaymentMethod>> METHODS = new HashMap<>();

    static {
        METHODS.put("CARD", CardPayment::new);
        METHODS.put("SWISH", SwishPayment::new);
        METHODS.put("CASH", CashPayment::new);
    }

    public static Optional<PaymentMethod> create(String paymentType) {
        if (paymentType == null) {
            return Optional.empty();
        }

        Supplier<PaymentMethod> supplier = METHODS.get(paymentType.toUpperCase(Locale.ROOT));

        if (supplier == null) {
            return Optional.empty();
        }

        return Optional.of(supplier.get());
    }
}