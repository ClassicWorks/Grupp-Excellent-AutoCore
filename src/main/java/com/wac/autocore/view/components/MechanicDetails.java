package com.wac.autocore.view.components;

import com.wac.autocore.model.Mechanic;
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

        Label idLabel = new Label(String.format("Mechanic ID: %d", mechanic.getId()));

        nameField.setText(mechanic.getName());
        phoneField.setText(mechanic.getPhone());
        specializationField.setText(mechanic.getSpecialization());

        Label availabilityLabel = new Label(String.format(
                "Status: %s", mechanic.isAvailable() ? "Available" : "Busy"));

        errorLabel.setStyle("-fx-text-fill: red;");

        VBox fields = new VBox(10,
                new Label("Name"),
                nameField,
                new Label("Phone"),
                phoneField,
                new Label("Specialization"),
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
        Button saveBtn = new Button("Save changes");

        saveBtn.disableProperty().bind(nameField.textProperty().isEmpty());

        saveBtn.setOnAction(e -> {
            errorLabel.setText("");

            if (nameField.getText().trim().isEmpty()) {
                errorLabel.setText("Name cannot be empty.");
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
