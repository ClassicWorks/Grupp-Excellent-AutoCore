package com.wac.autocore.view.components;

import com.wac.autocore.model.Invoice;
import com.wac.autocore.util.LanguageManager;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class InvoiceCard extends VBox {

    public InvoiceCard(Invoice invoice, Consumer<Invoice> onCardClick) {
        Label idLabel = new Label(String.format(LanguageManager.getString("invoice.id"), invoice.getId()));
        Label workOrderLabel = new Label(String.format(LanguageManager.getString("invoice.workorder"), invoice.getWorkOrder().getId()));
        Label dateLabel = new Label(invoice.getInvoiceDate().toString());
        Label totalLabel = new Label(String.format(
                LanguageManager.getString("invoice.details.amountFormat"), invoice.getTotalAmount()));
        Label statusLabel = new Label(invoice.isPaid()
                ? LanguageManager.getString("invoice.status.paid")
                : LanguageManager.getString("invoice.status.unpaid"));

        this.getChildren().addAll(
                idLabel,
                workOrderLabel,
                dateLabel,
                totalLabel,
                statusLabel);

        this.setOnMouseClicked(e -> onCardClick.accept(invoice));
        this.getStyleClass().add("card");
    }
}