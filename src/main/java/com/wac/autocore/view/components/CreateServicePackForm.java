package com.wac.autocore.view.components;

import com.wac.autocore.controller.CatalogController;
import com.wac.autocore.controller.Result;
import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.ServicePack;
import com.wac.autocore.util.ComboBoxLabels;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.util.ValueLabels;
import com.wac.autocore.view.UiResultHandler;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class CreateServicePackForm {
    private final CatalogController catalogController;
    private final UiResultHandler uiResultHandler;
    private final Stage popupStage;
    private List<ServiceItem> allServiceItems;
    private ObservableList<ServiceItem> chosenItems = FXCollections.observableArrayList();
    private Label errorLabel;
    private TextField nameField;
    private VBox chosenServicesRows;
    private ComboBox<ServiceItem> serviceComboBox;

    public CreateServicePackForm(Stage popupStage) {
        catalogController = ViewManager.getInstance().getControllers().catalog();
        uiResultHandler = new UiResultHandler();
        this.popupStage = popupStage;

        Result<List<ServiceItem>> result = uiResultHandler.handle(catalogController.getServiceItems());
        if(!result.isSuccess()) {
            popupStage.close();
            return;
        }
        allServiceItems = result.value();
    }

    public Parent show(){
        errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        Label title = new Label(LanguageManager.getString("servicePack.form.title"));
        title.getStyleClass().add("form-title");
        Label explanation = new Label(LanguageManager.getString("general.form.explanation"));
        explanation.setWrapText(true);
        explanation.setMinWidth(0);
        explanation.getStyleClass().add("form-explanation");
        VBox headerBox = new VBox(title, explanation);

        nameField = new TextField();
        nameField.setPromptText(LanguageManager.getString("servicePack.form.name.prompt"));
        VBox nameBox = new FormFieldContainer(
                LanguageManager.getString("servicePack.details.name.label"), nameField, true);

        // Show current services in pack
        chosenServicesRows = new VBox();
        chosenServicesRows.getStyleClass().add("row-container");
        VBox currentServicesContainer = new FormFieldContainer(
                LanguageManager.getString("servicePack.details.services.label"), chosenServicesRows, true);
        VBox addServiceSection = createAddServiceSection();

        //Get and set Service items
        refreshServiceItems();

        //Buttons
        Button saveBtn = createSaveBtn();

        Button cancelBtn = createCancelBtn();
        HBox btnContainer = new HBox(saveBtn, cancelBtn);
        btnContainer.getStyleClass().add("btn-container");

        VBox formContainer = new VBox(
                headerBox,
                nameBox,
                currentServicesContainer,
                addServiceSection,
                btnContainer
        );
        formContainer.getStyleClass().add("form-container");
        explanation.prefWidthProperty().bind(formContainer.widthProperty().subtract(40));

        ScrollPane scrollPane = new ScrollPane(formContainer);
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private void refreshServiceItems() {
        chosenServicesRows.getChildren().clear();

        //Add row for every chosen serviceItem
        boolean isLastItem = chosenItems.size() <= 1;
        if(chosenItems.isEmpty()){
            Label noItemsInListLabel = new Label(LanguageManager.getString("servicePack.form.emptyList"));
            chosenServicesRows.getChildren().add(noItemsInListLabel);
        }
        for (ServiceItem item : chosenItems) {
            chosenServicesRows.getChildren().add(createItemRow(item, isLastItem));
        }

        //Show total price and time for ServicePack
        double totalPrice = chosenItems.stream()
                .mapToDouble(ServiceItem::getPrice)
                .sum();
        int totalTime = chosenItems.stream()
                .mapToInt(ServiceItem::getEstimatedMinutes)
                .sum();

        Label priceAndTimeLabel = new Label(String.format(LanguageManager.getString("servicePack.totalPriceAndTime"),
                totalPrice,
                totalTime
        ));

        HBox finalRow = new HBox(priceAndTimeLabel);
        finalRow.getStyleClass().addAll("row", "row-final");
        finalRow.setAlignment(Pos.CENTER_RIGHT);
        chosenServicesRows.getChildren().add(finalRow);

        //Update combobox
        List<ServiceItem> availableServices = allServiceItems.stream()
                .filter(serviceItem -> !chosenItems.contains(serviceItem))
                .collect(Collectors.toList());

        serviceComboBox.getItems().setAll(FXCollections.observableArrayList(availableServices));
        serviceComboBox.setValue(null);
    }

    private Node createItemRow(ServiceItem item, boolean isLastItem) {
        Label nameLabel = new Label(ValueLabels.serviceName(item.getName()));
        Label priceLabel = new Label(String.format(LanguageManager.getString("service.priceAndTime"),
                item.getPrice(),
                item.getEstimatedMinutes())
        );

        Button removeBtn = new Button(LanguageManager.getString("workorder.edit.remove"));
        removeBtn.setDisable(isLastItem);
        removeBtn.getStyleClass().addAll("destroy-btn", "smaller");
        removeBtn.setOnAction(e -> {
            boolean success = chosenItems.remove(item);
            if(!success) {
                errorLabel.setText(String.format(LanguageManager.getString("servicePack.removeService.error"),
                        item.getId()));
                return;
            }
            refreshServiceItems();
        });

        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        HBox row = new HBox(nameLabel, priceLabel, removeBtn);
        row.getStyleClass().add("row");
        row.setMaxWidth(Double.MAX_VALUE);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox createAddServiceSection() {
        serviceComboBox = new ComboBox<>();
        serviceComboBox.setPromptText(LanguageManager.getString("servicePack.addService.prompt"));
        serviceComboBox.setConverter(ComboBoxLabels.serviceItem());

        Button addBtn = new Button(LanguageManager.getString("servicePack.addService.button"));
        addBtn.getStyleClass().add("confirm-btn");

        addBtn.disableProperty().bind(serviceComboBox.valueProperty().isNull());

        addBtn.setOnAction(e -> {
            ServiceItem selected = serviceComboBox.getValue();
            chosenItems.add(selected);

            refreshServiceItems();
        });

        HBox comboBoxContainer = new HBox(serviceComboBox, addBtn);
        comboBoxContainer.getStyleClass().add("row");
        return new FormFieldContainer(
                LanguageManager.getString("servicePack.addService.label"), comboBoxContainer, false);
    }

    private Button createSaveBtn() {
        Button saveBtn = new Button(LanguageManager.getString("servicePack.createNewPack"));
        saveBtn.getStyleClass().add("confirm-btn");
        saveBtn.disableProperty().bind(
                nameField.textProperty().isEmpty()
                        .or(Bindings.isEmpty(chosenItems))
        );
        saveBtn.setOnAction(e -> {
            Result<ServicePack> savedServicePackResult = uiResultHandler.handle(catalogController.createServicePack(
                    nameField.getText(),
                    chosenItems.stream().mapToInt(ServiceItem::getId).toArray()
            ));

            if(savedServicePackResult.isSuccess()){
                ServicePack savedServicePack = savedServicePackResult.value();
                popupStage.close();
                AppDialog.showInformation(
                        LanguageManager.getString("servicePack.form.success.title"),
                        LanguageManager.getString("servicePack.form.success.message"),
                        String.format(
                                LanguageManager.getString("servicePack.form.success.details"),
                                savedServicePack.getName(),
                                savedServicePack.getServiceItems().size()
                        )
                );
            }
        });
        return saveBtn;
    }

    private Button createCancelBtn() {
        Button cancelBtn = new Button(LanguageManager.getString("servicePack.cancel"));
        cancelBtn.getStyleClass().add("cancel-btn");
        cancelBtn.setOnAction(e -> popupStage.close());
        return cancelBtn;
    }
}
