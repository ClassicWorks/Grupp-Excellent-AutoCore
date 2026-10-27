package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class MechanicDetails extends BorderPane {

    private final Mechanic mechanic;
    private final Runnable onSaved;
    private final GarageSystem garageSystem = ViewManager.getInstance().getGarageSystem();

    private final TextField nameField = new TextField();
    private final TextField phoneField = new TextField();
    private final TextField specializationField = new TextField();
    private final Label errorLabel = new Label();

    public MechanicDetails(Mechanic mechanic, Runnable onSaved, Consumer<Integer> onDelete) {
        this.mechanic = mechanic;
        this.onSaved = onSaved;

        Label idLabel = new Label(String.format(LanguageManager.getString("mechanic.id"), mechanic.getId()));
        idLabel.getStyleClass().addAll("details-title");

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
        availabilityLabel.getStyleClass().addAll("status-label",
                mechanic.isAvailable() ? "completed" : "in-progress");

        errorLabel.getStyleClass().add("error-label");


        VBox fields = new VBox(
                nameBox,
                phoneBox,
                specializationBox,
                availabilityLabel,
                errorLabel
        );
        fields.getStyleClass().add("details-container");

        HBox confirmingButtons = new HBox(createSaveBtn());
        confirmingButtons.getStyleClass().add("btn-container");

        HBox destructiveButtons = new HBox(createDeleteBtn(onDelete));
        destructiveButtons.getStyleClass().add("btn-container");

        BorderPane actionableButtons = new BorderPane(null, null, destructiveButtons, null, confirmingButtons);

        setTop(idLabel);
        setCenter(fields);
        setBottom(actionableButtons);
    }

    private Button createSaveBtn() {
        Button saveBtn = new Button(LanguageManager.getString("mechanic.save"));
        saveBtn.getStyleClass().addAll("confirm-btn");

        saveBtn.disableProperty().bind(nameField.textProperty().isEmpty());

        saveBtn.setOnAction(e -> {
            errorLabel.setText("");
            errorLabel.getStyleClass().remove("success");

            if (nameField.getText().trim().isEmpty()) {
                errorLabel.setText(LanguageManager.getString("mechanic.error.nameEmpty"));
                return;
            }

            Mechanic updated = garageSystem.updateMechanic(
                    mechanic.getId(),
                    nameField.getText().trim(),
                    phoneField.getText().trim(),
                    specializationField.getText().trim());

            if (updated == null) {
                errorLabel.setText(LanguageManager.getString("mechanic.error.saveFailed"));
                return;
            }

            errorLabel.getStyleClass().add("success");
            errorLabel.setText(LanguageManager.getString("mechanic.saved"));
            onSaved.run();
        });
        return saveBtn;
    }

    private Button createDeleteBtn(Consumer<Integer> onDelete) {
        Button deleteBtn = new Button(LanguageManager.getString("mechanic.delete"));
        deleteBtn.getStyleClass().add("destroy-btn");
        deleteBtn.setOnAction(e -> onDelete.accept(mechanic.getId()));
        return deleteBtn;
    }
}