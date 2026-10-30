package com.wac.autocore.view.components;

import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.ServicePack;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.util.ValueLabels;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class ServicePackDetails extends BorderPane{
    private ServicePack servicePack;

    private Label errorLabel;
    private TextField nameField;

    public ServicePackDetails(ServicePack servicePack,
                              List<ServiceItem> allServiceItems,
                              Consumer<ServicePack> onSave,
                              Consumer<Integer> onDelete){
        this.servicePack = servicePack;

        errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        Label title = createTitle(ValueLabels.servicePackName(servicePack.getName()));

        Label explanation = new Label(LanguageManager.getString("general.form.explanation"));
        explanation.setWrapText(true);
        explanation.setMaxWidth(Double.MAX_VALUE);
        explanation.getStyleClass().add("form-explanation");

        Label idLabel = new Label(String.format("ID: %d", servicePack.getId()));

        nameField = new TextField(servicePack.getName());
        VBox nameBox = new FormFieldContainer(
                LanguageManager.getString("servicePack.details.name.label"), nameField, true);

        // ServiceItemSelection
        ServiceItemSelection serviceItemSelection = new ServiceItemSelection(
                servicePack.getServiceItems(),
                allServiceItems,
                errorMessage -> errorLabel.setText(errorMessage)
        );
        Label serviceItemselectionLabel = new Label(LanguageManager.getString("servicePack.details.services.label"));
        serviceItemselectionLabel.getStyleClass().add("form-field-label");
        serviceItemSelection.getChildren().addFirst(serviceItemselectionLabel);

        VBox detailsContainer = new VBox(
                explanation,
                idLabel,
                nameBox,
                serviceItemSelection,
                errorLabel
        );
        detailsContainer.getStyleClass().add("details-container");


        //Buttons
        Button saveBtn = createSaveBtn(onSave);
        HBox confirmingBtns = new HBox(saveBtn);
        confirmingBtns.getStyleClass().add("btn-container");

        Button deleteBtn = createDeleteBtn(onDelete);
        HBox destructiveBtns = new HBox(deleteBtn);
        destructiveBtns.getStyleClass().add("btn-container");

        BorderPane btnContainer = new BorderPane();
        btnContainer.setLeft(confirmingBtns);
        btnContainer.setRight(destructiveBtns);

        this.setTop(title);
        this.setCenter(detailsContainer);
        this.setBottom(btnContainer);
    }

    private Label createTitle(String name) {
        Label title = new Label(String.format(LanguageManager.getString("servicePack.details.title"),
                name));
        title.getStyleClass().add("details-title");

        return title;
    }

    private Button createSaveBtn(Consumer<ServicePack> onSave) {
        Button saveBtn = new Button(LanguageManager.getString("servicePack.save"));
        saveBtn.getStyleClass().add("confirm-btn");
        saveBtn.setOnAction(e -> {
            errorLabel.setText("");

            String nameValue = nameField.getText().trim();

            if (nameValue.isEmpty()) {
                errorLabel.setText(LanguageManager.getString("customer.form.error.nameRequired"));
                return;
            }
            servicePack.setName(nameValue);
            onSave.accept(servicePack);
        });
        return saveBtn;
    }

    private Button createDeleteBtn(Consumer<Integer> onDelete) {
        Button deleteBtn = new Button(LanguageManager.getString("servicePack.delete"));
        deleteBtn.getStyleClass().add("destroy-btn");
        deleteBtn.setOnAction(e -> onDelete.accept(servicePack.getId()));
        return deleteBtn;
    }
}
