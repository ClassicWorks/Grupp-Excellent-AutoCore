package com.wac.autocore.util;

import com.wac.autocore.model.WorkOrderStatus;

public class ValueLabels {

    public static String paymentType(String value) {
        return lookup("payment.type.", value, value);
    }

    public static String serviceName(String name) {
        return lookup("data.service.name.", name, name);
    }

    public static String serviceDescription(String name, String description) {
        return lookup("data.service.description.", name, description);
    }

    public static String specialization(String value) {
        return lookup("data.specialization.", value, value);
    }

    private static String lookup(String prefix, String value, String fallback) {
        if (value == null) {
            return fallback == null ? "" : fallback;
        }
        String key = prefix + normalize(value);
        return LanguageManager.getStringOrDefault(key, fallback == null ? "" : fallback);
    }

    public static String workOrderStatus(WorkOrderStatus status) {
        return lookup("workorder.status.", status.name(), status.name());
    }

    private static String normalize(String value) {
        return value.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
    }
}