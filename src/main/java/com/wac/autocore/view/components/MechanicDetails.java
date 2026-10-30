package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.controller.MechanicController;
import com.wac.autocore.controller.Result;
import com.wac.autocore.view.UiResultHandler;
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
    private final MechanicController mechanicController = ViewManager.getInstance().getControllers().mechanics();
    private final UiResultHandler uiResultHandler = new UiResultHandler();

    private final TextField nameField = new TextField();
    private final TextField phoneField = new TextField();
    private final TextField specializationField = new TextField();
    private final Label errorLabel = new Label();

    public MechanicDetails(Mechanic mechanic, Runnable onSaved, Consumer<Integer> onDelete) {
        this.mechanic = mechanic;
        this.onSaved = onSaved;

        Label idLabel = new Label(String.format(LanguageManager.getString("mechanic.id"), mechanic.getId()));
        idLabel.getStyleClass().addAll("details-title");

        Label explanation = new Label(LanguageManager.getString("general.form.explanation"));
        explanation.setWrapText(true);
        explanation.setMaxWidth(Double.MAX_VALUE);
        explanation.getStyleClass().add("form-explanation");

        nameField.setText(mechanic.getName());
        VBox nameBox = new FormFieldContainer(LanguageManager.getString("mechanic.name"), nameField, true);

        phoneField.setText(mechanic.getPhone());
        VBox phoneBox = new FormFieldContainer(LanguageManager.getString("mechanic.phone"), phoneField, false);

        specializationField.setText(mechanic.getSpecialization());
        VBox specializationBox = new FormFieldContainer(
                LanguageManager.getString("mechanic.specialization"), specializationField, false);

        Label availabilityLabel = new Label(String.format(
                LanguageManager.getString("mechanic.status"),
                mechanic.isAvailable()
                        ? LanguageManager.getString("mechanic.status.available")
                        : LanguageManager.getString("mechanic.status.busy")));
        availabilityLabel.getStyleClass().addAll("status-label",
                mechanic.isAvailable() ? "completed" : "in-progress");

        errorLabel.getStyleClass().add("error-label");


        VBox fields = new VBox(
                explanation,
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

            Result<Mechanic> result = uiResultHandler.handle(mechanicController.updateMechanic(
                    mechanic.getId(),
                    nameField.getText().trim(),
                    phoneField.getText().trim(),
                    specializationField.getText().trim()));

            if (!result.isSuccess()) {
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
