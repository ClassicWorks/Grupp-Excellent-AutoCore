package com.wac.autocore.view.components;

import com.wac.autocore.model.Invoice;
import com.wac.autocore.util.LanguageManager;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class InvoiceCard extends HBox {

    public InvoiceCard(Invoice invoice, Consumer<Invoice> onCardClick) {
        VBox invoiceCard = new VBox();
        invoiceCard.getStyleClass().add("invoice-card");

        Label idLabel = new Label(String.format(LanguageManager.getString("invoice.id"), invoice.getId()));
        Label workOrderLabel = new Label(String.format(LanguageManager.getString("invoice.workorder"), invoice.getWorkOrder().getId()));
        Label dateLabel = new Label(invoice.getInvoiceDate().toString());
        Label totalLabel = new Label(invoice.getTotalAmount() + " SEK");
        Label statusLabel = new Label(invoice.isPaid()
                ? LanguageManager.getString("invoice.status.paid")
                : LanguageManager.getString("invoice.status.unpaid"));

        invoiceCard.getChildren().addAll(idLabel, workOrderLabel, dateLabel, totalLabel, statusLabel);

        this.getChildren().add(invoiceCard);
        this.setCursor(Cursor.HAND);
        this.setOnMouseClicked(e -> onCardClick.accept(invoice));
    }
}