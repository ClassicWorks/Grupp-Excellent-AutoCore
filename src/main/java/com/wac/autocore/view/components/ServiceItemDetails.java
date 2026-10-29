package com.wac.autocore.view.components;

import com.wac.autocore.controller.CatalogController;
import com.wac.autocore.controller.Result;
import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.util.ValueLabels;
import com.wac.autocore.view.UiResultHandler;
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
    private final CatalogController catalogController = ViewManager.getInstance().getControllers().catalog();
    private final UiResultHandler uiResultHandler = new UiResultHandler();
    private final Runnable onSaved;

    public ServiceItemDetails(ServiceItem serviceItem, Runnable onSaved) {
        this.serviceItem = serviceItem;
        this.onSaved = onSaved;

        Label nameLabel = new Label(String.format(
                LanguageManager.getString("service.details.title"),
                ValueLabels.serviceName(serviceItem.getName()))
        );
        nameLabel.getStyleClass().add("details-title");

        Label explanation = new Label(LanguageManager.getString("general.form.explanation"));
        explanation.setWrapText(true);
        explanation.setMaxWidth(Double.MAX_VALUE);
        explanation.getStyleClass().add("form-explanation");

        Label descriptionLabel = new Label(ValueLabels.serviceDescription(serviceItem.getName(), serviceItem.getDescription()));
        descriptionLabel.setWrapText(true);
        VBox descriptionBox = new FormFieldContainer(
                LanguageManager.getString("service.description"), descriptionLabel, false);

        Label timeLabel = new Label(String.format(LanguageManager.getString("service.minutes"), serviceItem.getEstimatedMinutes()));
        VBox timeBox = new FormFieldContainer(
                LanguageManager.getString("service.estimatedTime"), timeLabel, false);

        priceField.setText(String.valueOf(serviceItem.getPrice()));
        VBox priceBox = new FormFieldContainer(LanguageManager.getString("service.price"), priceField, true);


        errorLabel.getStyleClass().add("error-label");

        VBox fields = new VBox(
                explanation,
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
            errorLabel.getStyleClass().remove("success");

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

            Result<ServiceItem> updatedResult = uiResultHandler.handle(
                    catalogController.updateServiceItemPrice(serviceItem.getId(), newPrice)
            );
            if (!updatedResult.isSuccess()) {
                errorLabel.setText(LanguageManager.getString("service.error.saveFailed"));
                return;
            }

            errorLabel.getStyleClass().add("success");
            errorLabel.setText(LanguageManager.getString("service.saved"));
            onSaved.run();
        });

        return saveBtn;
    }
}