package com.wac.autocore.view.components;

import com.wac.autocore.model.Payment;
import com.wac.autocore.util.LanguageManager;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PaymentCard extends HBox {

    public PaymentCard(Payment payment) {
        VBox paymentCard = new VBox();
        paymentCard.getStyleClass().add("payment-card");

        Label invoiceLabel = new Label(String.format(LanguageManager.getString("invoice.id"), payment.getInvoice().getId()));
        Label amountLabel = new Label(payment.getAmount() + " SEK");
        Label typeLabel = new Label(payment.getPaymentType());
        Label dateLabel = new Label(payment.getPaymentDate().toString());
        Label statusLabel = new Label(payment.isSuccessful()
                ? LanguageManager.getString("payment.status.successful")
                : LanguageManager.getString("payment.status.failed"));

        paymentCard.getChildren().addAll(invoiceLabel, amountLabel, typeLabel, dateLabel, statusLabel);

        this.getChildren().add(paymentCard);
    }
}