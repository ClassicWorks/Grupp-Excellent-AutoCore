package com.wac.autocore.util;

import com.wac.autocore.model.*;
import javafx.util.StringConverter;

import java.util.function.Function;

/**
 * Translated texts for items shown in ComboBoxes.
 * Use with comboBox.setConverter(ComboBoxLabels.mechanic()) etc,
 * so the items follow the selected language instead of the models toString().
 */
public class ComboBoxLabels {

    private static <T> StringConverter<T> converter(Function<T, String> labelFunction) {
        return new StringConverter<T>() {
            @Override
            public String toString(T item) {
                return item == null ? "" : labelFunction.apply(item);
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        };
    }

    public static StringConverter<Mechanic> mechanic() {
        return converter(ComboBoxLabels::mechanicLabel);
    }

    public static StringConverter<Customer> customer() {
        return converter(ComboBoxLabels::customerLabel);
    }

    public static StringConverter<WorkOrder> workOrder() {
        return converter(ComboBoxLabels::workOrderLabel);
    }

    public static StringConverter<Invoice> invoice() {
        return converter(ComboBoxLabels::invoiceLabel);
    }

    private static String mechanicLabel(Mechanic mechanic) {
        String status = mechanic.isAvailable()
                ? LanguageManager.getString("mechanic.status.available")
                : LanguageManager.getString("mechanic.status.busy");

        return String.format("%d - %s | %s | %s",
                mechanic.getId(),
                mechanic.getName(),
                mechanic.getSpecialization(),
                status);
    }

    private static String customerLabel(Customer customer) {
        String label = String.format("%d - %s | %s | %s",
                customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail());

        if (customer.isVip()) {
            label += " | " + LanguageManager.getString("customer.vip");
        }
        return label;
    }

    private static String workOrderLabel(WorkOrder workOrder) {
        String label = String.format(LanguageManager.getString("invoice.workorder"), workOrder.getId());

        if (workOrder.getBooking() != null && workOrder.getBooking().getVehicle() != null) {
            label += " | " + workOrder.getBooking().getVehicle().getRegistrationNumber();
        }
        return label;
    }

    private static String invoiceLabel(Invoice invoice) {
        String label = String.format(LanguageManager.getString("invoice.id"), invoice.getId());

        if (invoice.getWorkOrder() != null) {
            label += " | " + String.format(LanguageManager.getString("invoice.workorder"), invoice.getWorkOrder().getId());
        }
        return label + " | " + invoice.getTotalAmount() + " SEK";
    }

    private static String serviceItemLabel(ServiceItem serviceItem) {
        return String.format("%s | %.0f kr", serviceItem.getName(), serviceItem.getPrice());
    }

    public static StringConverter<ServiceItem> serviceItem() {
        return converter(ComboBoxLabels::serviceItemLabel);
    }
}