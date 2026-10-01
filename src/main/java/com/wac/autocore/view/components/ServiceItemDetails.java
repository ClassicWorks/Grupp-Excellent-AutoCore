package com.wac.autocore.view.components;

import com.wac.autocore.model.ServiceItem;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class ServiceItemDetails extends BorderPane {

    private final ServiceItem serviceItem;

    private final TextField priceField = new TextField();
    private final Label errorLabel = new Label();

    public ServiceItemDetails(ServiceItem serviceItem) {
        this.serviceItem = serviceItem;

        Label nameLabel = new Label(serviceItem.getName());
        nameLabel.getStyleClass().add("details-heading");

        Label descriptionLabel = new Label(serviceItem.getDescription());
        descriptionLabel.setWrapText(true);

        Label timeLabel = new Label(String.format("%d min", serviceItem.getEstimatedMinutes()));

        priceField.setText(String.valueOf(serviceItem.getPrice()));

        VBox fields = new VBox(10,
                new Label("Description"),
                descriptionLabel,
                new Label("Estimated time"),
                timeLabel,
                new Label("Price (kr)"),
                priceField,
                errorLabel
        );

        errorLabel.setStyle("-fx-text-fill: red;");

        HBox actionableButtons = new HBox(20, createSaveBtn());

        this.setTop(nameLabel);
        this.setCenter(fields);
        this.setBottom(actionableButtons);
    }

    private Button createSaveBtn() {
        Button saveBtn = new Button("Save changes");

        saveBtn.disableProperty().bind(priceField.textProperty().isEmpty());

        saveBtn.setOnAction(e -> {
            errorLabel.setText("");

            String input = priceField.getText().trim().replace(',', '.');

            double newPrice;
            try {
                newPrice = Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                errorLabel.setText("Price must be a number.");
                return;
            }

            if (newPrice < 0) {
                errorLabel.setText("Price cannot be negative.");
                return;
            }

            //TODO Call garageSystem when an update method for price exists
            System.out.printf("Should call garageSystem.updateServiceItemPrice(%d, %s)%n",
                    serviceItem.getId(), newPrice);
        });

        return saveBtn;
    }
}
