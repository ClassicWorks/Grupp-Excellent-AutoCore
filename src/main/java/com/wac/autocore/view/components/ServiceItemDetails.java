package com.wac.autocore.view.components;

import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
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

        Label nameLabel = new Label(serviceItem.getName());
        nameLabel.getStyleClass().add("details-heading");

        Label descriptionLabel = new Label(serviceItem.getDescription());
        descriptionLabel.setWrapText(true);

        Label timeLabel = new Label(String.format(LanguageManager.getString("service.minutes"), serviceItem.getEstimatedMinutes()));

        priceField.setText(String.valueOf(serviceItem.getPrice()));

        VBox fields = new VBox(10,
                new Label(LanguageManager.getString("service.description")),
                descriptionLabel,
                new Label(LanguageManager.getString("service.estimatedTime")),
                timeLabel,
                new Label(LanguageManager.getString("service.price")),
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
        Button saveBtn = new Button(LanguageManager.getString("service.save"));

        saveBtn.disableProperty().bind(priceField.textProperty().isEmpty());

        saveBtn.setOnAction(e -> {
            errorLabel.setText("");
            errorLabel.setStyle("-fx-text-fill: red;");

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