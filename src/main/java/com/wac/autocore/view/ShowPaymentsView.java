package com.wac.autocore.view;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Payment;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.components.PaymentCard;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class ShowPaymentsView {

    public Parent show() {
        BorderPane layout = new BorderPane();
        layout.setTop(getHeader());

        VBox paymentsBox = new VBox();

        for (Payment payment : new GarageSystem().getPayments()) {
            PaymentCard paymentCard = new PaymentCard(payment);
            paymentsBox.getChildren().add(paymentCard);
        }

        ScrollPane paymentsScroll = new ScrollPane(paymentsBox);
        paymentsScroll.setFitToWidth(true);

        layout.setCenter(paymentsScroll);

        return layout;
    }

    private Node getHeader() {
        BorderPane header = new BorderPane();
        Label title = new Label(LanguageManager.getString("payments.title"));
        Button processPaymentBtn = new Button(LanguageManager.getString("payments.create"));
        header.setCenter(title);
        header.setRight(processPaymentBtn);
        processPaymentBtn.setOnAction(e -> ViewManager.getInstance().showProcessPaymentPopup());
        return header;
    }
}