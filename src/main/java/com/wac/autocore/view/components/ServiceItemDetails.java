package com.wac.autocore.view.components;

import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.util.ValueLabels;
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
    private final GarageSystem garageSystem = new GarageSystem();
    private final Runnable onSaved;

    public ServiceItemDetails(ServiceItem serviceItem, Runnable onSaved) {
        this.serviceItem = serviceItem;
        this.onSaved = onSaved;

        Label nameLabel = new Label(String.format(
                LanguageManager.getString("service.details.title"),
                ValueLabels.serviceName(serviceItem.getName()))
        );
        nameLabel.getStyleClass().add("details-title");

        Label descriptionFieldLabel = new Label(LanguageManager.getString("service.description"));
        descriptionFieldLabel.getStyleClass().add("form-field-label");

        Label descriptionLabel = new Label(ValueLabels.serviceDescription(serviceItem.getName(), serviceItem.getDescription()));
        descriptionLabel.setWrapText(true);

        VBox descriptionBox = new VBox(
                descriptionFieldLabel,
                descriptionLabel
        );
        descriptionBox.getStyleClass().add("form-field-container");


        Label timeFieldLabel = new Label(LanguageManager.getString("service.estimatedTime"));
        timeFieldLabel.getStyleClass().add("form-field-label");

        Label timeLabel = new Label(String.format(LanguageManager.getString("service.minutes"), serviceItem.getEstimatedMinutes()));

        VBox timeBox = new VBox(
                timeFieldLabel,
                timeLabel
        );
        timeBox.getStyleClass().add("form-field-container");


        Label priceFieldLabel = new Label(LanguageManager.getString("service.price"));
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
        Button saveBtn = new Button(LanguageManager.getString("service.save"));
        saveBtn.getStyleClass().add("confirm-btn");

        saveBtn.disableProperty().bind(priceField.textProperty().isEmpty());

        saveBtn.setOnAction(e -> {
            errorLabel.setText("");

            String input = priceField.getText().trim().replace(',', '.');

            double newPrice;
            try {
                newPrice = Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                errorLabel.setText(LanguageManager.getString("service.error.priceNumber"));
                return;
            }

            if (newPrice < 0) {
                errorLabel.setText(LanguageManager.getString("service.error.priceNegative"));
                return;
            }

            ServiceItem updated = garageSystem.updateServiceItemPrice(serviceItem.getId(), newPrice);
            if (updated == null) {
                errorLabel.setText(LanguageManager.getString("service.error.saveFailed"));
                return;
            }

            errorLabel.setStyle("-fx-text-fill: green;");
            errorLabel.setText(LanguageManager.getString("service.saved"));
            onSaved.run();
        });

        return saveBtn;
    }
}