package com.wac.autocore.view.components;

import com.wac.autocore.model.Mechanic;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class MechanicDetailForm {

    public static Node getForm(Mechanic mechanic) {

        VBox form = new VBox(10);

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField(mechanic.getName());

        Label phoneLabel = new Label("Phone:");
        TextField phoneField = new TextField(mechanic.getPhone());

        Label specializationLabel = new Label("Specialization:");
        TextField specializationField = new TextField(mechanic.getSpecialization());

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button saveBtn = new Button("Save");

        saveBtn.setOnAction(e -> {
            String nameValue = nameField.getText().trim();

            if (nameValue.isEmpty()) {
                errorLabel.setText("Name cannot be empty.");
                return;
            }

            mechanic.setName(nameValue);
            mechanic.setPhone(phoneField.getText().trim());
            mechanic.setSpecialization(specializationField.getText().trim());

            errorLabel.setText("Saved!");
        });

        form.getChildren().addAll(
                nameLabel, nameField,
                phoneLabel, phoneField,
                specializationLabel, specializationField,
                saveBtn, errorLabel
        );

        return form;
    }
}
