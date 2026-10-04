package com.wac.autocore.view.components;

import com.wac.autocore.model.Mechanic;
import com.wac.autocore.util.LanguageManager;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MechanicDetails extends BorderPane {

    private final Mechanic mechanic;

    private final TextField nameField = new TextField();
    private final TextField phoneField = new TextField();
    private final TextField specializationField = new TextField();
    private final Label errorLabel = new Label();

    public MechanicDetails(Mechanic mechanic) {
        this.mechanic = mechanic;

        Label idLabel = new Label(String.format(LanguageManager.getString("mechanic.id"), mechanic.getId()));

        Label nameLabel = new Label(LanguageManager.getString("mechanic.name"));
        nameLabel.getStyleClass().add("form-field-label");

        nameField.setText(mechanic.getName());

        VBox nameBox = new VBox(nameLabel, nameField);
        nameBox.getStyleClass().add("form-field-container");

        Label phoneLabel = new Label(LanguageManager.getString("mechanic.phone"));
        phoneLabel.getStyleClass().add("form-field-label");

        phoneField.setText(mechanic.getPhone());

        VBox phoneBox = new VBox(phoneLabel, phoneField);
        phoneBox.getStyleClass().add("form-field-container");

        Label specializationLabel = new Label(LanguageManager.getString("mechanic.specialization"));
        specializationLabel.getStyleClass().add("form-field-label");

        specializationField.setText(mechanic.getSpecialization());
        VBox specializationBox = new VBox(specializationLabel, specializationField);
        specializationBox.getStyleClass().add("form-field-container");

        Label availabilityLabel = new Label(String.format(
                LanguageManager.getString("mechanic.status"),
                mechanic.isAvailable()
                        ? LanguageManager.getString("mechanic.status.available")
                        : LanguageManager.getString("mechanic.status.busy")));
        availabilityLabel.getStyleClass().add("status-label");
        //TODO add styling and logic for status-label

        errorLabel.setStyle("-fx-text-fill: red;");

        errorLabel.getStyleClass().add("error-label");


        VBox fields = new VBox(
                nameBox,
                phoneBox,
                specializationBox,
                availabilityLabel,
                errorLabel
        );


        HBox actionableButtons = new HBox(createSaveBtn());
        actionableButtons.getStyleClass().add("btn-container");

        setTop(idLabel);
        setCenter(fields);
        setBottom(actionableButtons);

        getStyleClass().add("details-container");
    }

    private Button createSaveBtn() {
        Button saveBtn = new Button(LanguageManager.getString("mechanic.save"));
        saveBtn.getStyleClass().addAll("confirm-btn");

        saveBtn.disableProperty().bind(nameField.textProperty().isEmpty());

        saveBtn.setOnAction(e -> {
            errorLabel.setText("");

            if (nameField.getText().trim().isEmpty()) {
                errorLabel.setText(LanguageManager.getString("mechanic.error.nameEmpty"));
                return;
            }

            System.out.printf("Should call garageSystem.updateMechanic(%d, %s, %s, %s)%n",
                    mechanic.getId(),
                    nameField.getText().trim(),
                    phoneField.getText().trim(),
                    specializationField.getText().trim());
        });
        return saveBtn;
    }
}