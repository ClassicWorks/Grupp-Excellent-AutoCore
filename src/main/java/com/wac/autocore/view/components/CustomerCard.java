package com.wac.autocore.view.components;

import com.wac.autocore.model.Customer;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CustomerCard extends HBox {

    public CustomerCard(Customer customer) {
        IconImageView customerIcon = new IconImageView("/imgs/user-solid.png", 40, 40);

        Label nameLabel = new Label(customer.getName());
        Label contactLabel = new Label(String.format("%s | %s", customer.getPhone(), customer.getEmail()));

        VBox customerInfoBox = new VBox(nameLabel, contactLabel);

        if (customer.isVip()) {
            Label vipLabel = new Label("VIP");
            customerInfoBox.getChildren().add(vipLabel);
        }

        HBox customerBox = new HBox(customerIcon, customerInfoBox);

        VBox customerCard = new VBox();
        customerCard.setStyle("-fx-border-color: blue");
        customerCard.getStyleClass().add("customer-card");
        customerCard.getChildren().add(customerBox);

        this.getChildren().addAll(customerCard);
    }
}