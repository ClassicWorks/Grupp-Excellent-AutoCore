package com.wac.autocore.view.components;

import com.wac.autocore.model.Customer;
import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class CustomerCard extends VBox {

    public CustomerCard(Customer customer) {
        ImageViewWithAltText customerIcon = new ImageViewWithAltText("/imgs/user-solid.png", 40, 40,
                LanguageManager.getString("image.user-solid"));

        Label nameLabel = new Label(customer.getName());
        Label contactLabel = new Label(String.format("%s | %s", customer.getPhone(), customer.getEmail()));

        VBox customerInfoBox = new VBox(nameLabel, contactLabel);
        customerInfoBox.getStyleClass().addAll("card-info-box");

        if (customer.isVip()) {
            Label vipLabel = new Label(LanguageManager.getString("customer.vip"));
            customerInfoBox.getChildren().add(vipLabel);
        }

        HBox customerBox = new HBox(customerIcon, customerInfoBox);

        this.getStyleClass().add("card");
        this.getChildren().addAll(customerBox);
    }

    public CustomerCard(Customer customer, Consumer<Customer> onCreateVehicle){
        this(customer);

        Button createVehicleBtn = new Button(LanguageManager.getString("customer.card.createVehicle"));
        createVehicleBtn.getStyleClass().addAll("confirm-btn",  "card-action-btn");

        createVehicleBtn.setOnAction(e -> onCreateVehicle.accept(customer));

        HBox buttonBox = new HBox(createVehicleBtn);
        buttonBox.setAlignment(Pos.BASELINE_RIGHT);
        this.getChildren().add(buttonBox);
    }
}