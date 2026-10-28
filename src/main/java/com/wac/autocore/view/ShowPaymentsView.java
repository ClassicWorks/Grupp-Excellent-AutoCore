package com.wac.autocore.view;

import com.wac.autocore.controller.PaymentController;
import com.wac.autocore.controller.Result;
import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Payment;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.components.PaymentCard;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class ShowPaymentsView {
    private final PaymentController paymentController;

    public ShowPaymentsView() {
        paymentController = ViewManager.getInstance().getControllers().payments();
    }

    public Parent show() {
        BorderPane layout = new BorderPane();
        Node header = getHeader();
        header.getStyleClass().add("content-header-container");
        layout.setTop(header);

        VBox paymentsBox = new VBox();
        paymentsBox.getStyleClass().add("card-container");

        Result<List<Payment>> result = paymentController.getPayments();
        if (result.isSuccess()) {
            for (Payment payment : result.value()) {
                paymentsBox.getChildren().add(new PaymentCard(payment));
            }
        } else {
            Label errorLabel = new Label(result.error());
            errorLabel.getStyleClass().add("error-label");
            errorLabel.setWrapText(true);
            paymentsBox.getChildren().add(errorLabel);
        }

        ScrollPane paymentsScroll = new ScrollPane(paymentsBox);
        paymentsScroll.setFitToWidth(true);
        paymentsScroll.setFitToHeight(true);

        layout.setCenter(paymentsScroll);

        return layout;
    }

    private Node getHeader() {
        BorderPane header = new BorderPane();
        Label title = new Label(LanguageManager.getString("payments.title"));
        title.getStyleClass().add("page-title");

        Button processPaymentBtn = new Button(LanguageManager.getString("payments.create"));
        processPaymentBtn.getStyleClass().add("confirm-btn");

        header.setCenter(title);
        header.setRight(processPaymentBtn);
        processPaymentBtn.setOnAction(e -> ViewManager.getInstance().showProcessPaymentPopup());
        return header;
    }
}