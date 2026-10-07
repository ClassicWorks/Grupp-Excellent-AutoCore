package com.wac.autocore.view.components;

import com.wac.autocore.model.Customer;
import com.wac.autocore.util.LanguageManager;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CustomerCard extends VBox {

    public CustomerCard(Customer customer) {
        ImageViewWithAltText customerIcon = new ImageViewWithAltText("/imgs/user-solid.png", 40, 40,
                LanguageManager.getString("image.user-solid"));

        Label nameLabel = new Label(customer.getName());
        Label contactLabel = new Label(String.format("%s | %s", customer.getPhone(), customer.getEmail()));

        VBox customerInfoBox = new VBox(nameLabel, contactLabel);

        if (customer.isVip()) {
            Label vipLabel = new Label(LanguageManager.getString("customer.vip"));
            customerInfoBox.getChildren().add(vipLabel);
        }

        HBox customerBox = new HBox(customerIcon, customerInfoBox);

        this.getStyleClass().add("card");
        this.getChildren().add(customerBox);
    }
}