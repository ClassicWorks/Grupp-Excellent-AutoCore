package com.wac.autocore.view.components;

import com.wac.autocore.model.Invoice;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class InvoiceCard extends HBox {

    public InvoiceCard(Invoice invoice) {
        VBox invoiceCard = new VBox();
        invoiceCard.getStyleClass().add("invoice-card");

        Label idLabel = new Label("Invoice #" + invoice.getId());
        Label workOrderLabel = new Label("Work order #" + invoice.getWorkOrder().getId());
        Label dateLabel = new Label(invoice.getInvoiceDate().toString());
        Label totalLabel = new Label(invoice.getTotalAmount() + " SEK");
        Label statusLabel = new Label(invoice.isPaid() ? "Paid" : "Unpaid");

        invoiceCard.getChildren().addAll(idLabel, workOrderLabel, dateLabel, totalLabel, statusLabel);

        this.getChildren().add(invoiceCard);
    }
}