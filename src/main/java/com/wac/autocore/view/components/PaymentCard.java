package com.wac.autocore.view.components;

import com.wac.autocore.model.Payment;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.util.ValueLabels;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PaymentCard extends VBox {

    public PaymentCard(Payment payment) {
        VBox paymentCard = new VBox();

        Label invoiceLabel = new Label(String.format(LanguageManager.getString("invoice.id"), payment.getInvoice().getId()));
        Label amountLabel = new Label(payment.getAmount() + " SEK");
        Label typeLabel = new Label(ValueLabels.paymentType(payment.getPaymentType()));
        Label dateLabel = new Label(payment.getPaymentDate().toString());
        Label statusLabel = new Label(payment.isSuccessful()
                ? LanguageManager.getString("payment.status.successful")
                : LanguageManager.getString("payment.status.failed"));
        statusLabel.getStyleClass().addAll("status-label");
        //TODO add logic and styling to status-label

        paymentCard.getChildren().addAll(invoiceLabel, amountLabel, typeLabel, dateLabel, statusLabel);
        paymentCard.getStyleClass().addAll("card-info-box");

        this.getChildren().add(paymentCard);
        this.getStyleClass().addAll("card");
    }
}