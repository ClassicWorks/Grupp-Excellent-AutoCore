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

        nameField.setText(mechanic.getName());
        phoneField.setText(mechanic.getPhone());
        specializationField.setText(mechanic.getSpecialization());

        Label availabilityLabel = new Label(String.format(
                LanguageManager.getString("mechanic.status"),
                mechanic.isAvailable()
                        ? LanguageManager.getString("mechanic.status.available")
                        : LanguageManager.getString("mechanic.status.busy")));

        errorLabel.setStyle("-fx-text-fill: red;");

        VBox fields = new VBox(10,
                new Label(LanguageManager.getString("mechanic.name")),
                nameField,
                new Label(LanguageManager.getString("mechanic.phone")),
                phoneField,
                new Label(LanguageManager.getString("mechanic.specialization")),
                specializationField,
                availabilityLabel,
                errorLabel
        );

        HBox actionableButtons = new HBox(20, createSaveBtn());

        this.setTop(idLabel);
        this.setCenter(fields);
        this.setBottom(actionableButtons);
    }

    private Button createSaveBtn() {
        Button saveBtn = new Button(LanguageManager.getString("mechanic.save"));

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