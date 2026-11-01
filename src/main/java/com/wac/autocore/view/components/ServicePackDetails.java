package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.ServicePack;
import com.wac.autocore.security.Permission;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.util.ValueLabels;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class ServicePackDetails extends BorderPane{
    private ServicePack servicePack;
    private final boolean canEdit;

    private Label errorLabel;
    private TextField nameField;

    public ServicePackDetails(ServicePack servicePack,
                              List<ServiceItem> allServiceItems,
                              Consumer<ServicePack> onSave,
                              Consumer<Integer> onDelete){
        this.servicePack = servicePack;
        canEdit = ViewManager.getInstance().getAccessGuard().has(Permission.MANAGE_CATALOG);

        errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        Label title = createTitle(ValueLabels.servicePackName(servicePack.getName()));

        Label idLabel = new Label(String.format("ID: %d", servicePack.getId()));

        nameField = new TextField(servicePack.getName());
        Label nameLabel = new Label(servicePack.getName());
        VBox nameBox = new FormFieldContainer(
                LanguageManager.getString("servicePack.details.name.label"),
                canEdit ? nameField : nameLabel,
                canEdit);

        // ServiceItemSection
        VBox serviceItemSection;
        if(canEdit) {
            ServiceItemSelection serviceItemSelection = new ServiceItemSelection(
                    servicePack.getServiceItems(),
                    allServiceItems,
                    errorMessage -> errorLabel.setText(errorMessage)
            );
            Label serviceItemselectionLabel = new Label(LanguageManager.getString("servicePack.details.services.label"));
            serviceItemselectionLabel.getStyleClass().add("form-field-label");
            serviceItemSelection.getChildren().addFirst(serviceItemselectionLabel);
            serviceItemSection  = new VBox(serviceItemSelection);
        }else {
            serviceItemSection = new FormFieldContainer(
                    LanguageManager.getString("servicePack.details.services.label"),
                    createCurrentServices(),
                    false);
        }

        VBox detailsContainer = new VBox(
                //explanation,
                idLabel,
                nameBox,
                serviceItemSection,
                errorLabel
        );
        detailsContainer.getStyleClass().add("details-container");

        if (canEdit) {
            Label explanation = new Label(LanguageManager.getString("general.form.explanation"));
            explanation.setWrapText(true);
            explanation.getStyleClass().add("form-explanation");
            detailsContainer.getChildren().addFirst(explanation);
        }

        //Buttons
        if(canEdit) {
            Button saveBtn = createSaveBtn(onSave);
            HBox confirmingBtns = new HBox(saveBtn);
            confirmingBtns.getStyleClass().add("btn-container");

            Button deleteBtn = createDeleteBtn(onDelete);
            HBox destructiveBtns = new HBox(deleteBtn);
            destructiveBtns.getStyleClass().add("btn-container");

            BorderPane btnContainer = new BorderPane();
            btnContainer.setLeft(confirmingBtns);
            btnContainer.setRight(destructiveBtns);

            this.setBottom(btnContainer);
        }

        this.setTop(title);
        this.setCenter(detailsContainer);
    }

    public VBox createCurrentServices (){
        VBox servicesRows = new VBox();
        if(servicePack.getServiceItems().isEmpty()){
            Label noItemsInListLabel = new Label(LanguageManager.getString("servicePack.form.emptyList"));
            servicesRows.getChildren().add(noItemsInListLabel);
        }
        for (ServiceItem item : servicePack.getServiceItems()) {
            servicesRows.getChildren().add(createItemRow(item));
        }

        //Show total price and time
        double totalPrice = servicePack.getServiceItems().stream()
                .mapToDouble(ServiceItem::getPrice)
                .sum();
        int totalTime = servicePack.getServiceItems().stream()
                .mapToInt(ServiceItem::getEstimatedMinutes)
                .sum();

        Label priceAndTimeLabel = new Label(
                String.format(LanguageManager.getString("servicePack.totalPriceAndTime"),
                totalPrice,
                totalTime
        ));

        HBox finalRow = new HBox(priceAndTimeLabel);
        finalRow.getStyleClass().addAll("row", "row-final");
        finalRow.setAlignment(Pos.CENTER_RIGHT);
        servicesRows.getChildren().add(finalRow);
        return servicesRows;
    }

    private Node createItemRow(ServiceItem item) {
        Label nameLabel = new Label(ValueLabels.serviceName(item.getName()));
        Label priceLabel = new Label(String.format(LanguageManager.getString("service.priceAndTime"),
                item.getPrice(), item.getEstimatedMinutes())
        );

        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        HBox row = new HBox(nameLabel, priceLabel);
        row.getStyleClass().add("row");
        row.setMaxWidth(Double.MAX_VALUE);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
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
