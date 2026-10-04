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
        nameLabel.getStyleClass().add("details-title");
        //TODO add styling and implement details-title

        Label descriptionFieldLabel = new Label("Description");
        descriptionFieldLabel.getStyleClass().add("form-field-label");

        Label descriptionLabel = new Label(serviceItem.getDescription());
        descriptionLabel.setWrapText(true);

        VBox descriptionBox = new VBox(
                descriptionFieldLabel,
                descriptionLabel
        );
        descriptionBox.getStyleClass().add("form-field-container");


        Label timeFieldLabel = new Label("Estimated time");
        timeFieldLabel.getStyleClass().add("form-field-label");

        Label timeLabel = new Label(
                String.format("%d min", serviceItem.getEstimatedMinutes())
        );

        VBox timeBox = new VBox(
                timeFieldLabel,
                timeLabel
        );
        timeBox.getStyleClass().add("form-field-container");


        Label priceFieldLabel = new Label("Price (kr)");
        priceFieldLabel.getStyleClass().add("form-field-label");

        priceField.setText(String.valueOf(serviceItem.getPrice()));

        VBox priceBox = new VBox(
                priceFieldLabel,
                priceField
        );
        priceBox.getStyleClass().add("form-field-container");


        errorLabel.getStyleClass().add("error-label");

        VBox fields = new VBox(
                descriptionBox,
                timeBox,
                priceBox,
                errorLabel
        );
        fields.getStyleClass().add("details-container");


        HBox actionableButtons = new HBox(createSaveBtn());
        actionableButtons.getStyleClass().add("btn-container");


        this.setTop(nameLabel);
        this.setCenter(fields);
        this.setBottom(actionableButtons);
    }

    private Button createSaveBtn() {
        Button saveBtn = new Button("Save changes");
        saveBtn.getStyleClass().add("confirm-btn");

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
