package com.wac.autocore.view;

import com.wac.autocore.data.Database;
import com.wac.autocore.model.Payment;
import com.wac.autocore.view.components.PaymentCard;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class ShowPaymentsView {

    public Parent show() {
        VBox layout = new VBox();

        VBox paymentsBox = new VBox();

        for (Payment payment : Database.getPayments()) {
            PaymentCard paymentCard = new PaymentCard(payment);
            paymentsBox.getChildren().add(paymentCard);
        }

        ScrollPane paymentsScroll = new ScrollPane(paymentsBox);
        paymentsScroll.setFitToWidth(true);

        layout.getChildren().add(paymentsScroll);

        return layout;
    }
}