package com.wac.autocore.view.components;

import com.wac.autocore.exceptions.AutoCoreException;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.ServicePack;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.model.WorkOrderItem;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.ComboBoxLabels;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.util.ValueLabels;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class EditWorkOrderItemsForm {

    private final GarageSystem garageSystem;
    private final Stage popupStage;
    private final int workOrderId;

    private final VBox content = new VBox();
    private final Label errorLabel = new Label();

    private WorkOrder workOrder;
    private final List<ServiceItem> addedServices = new ArrayList<>();
    private final List<Integer> removedItemIds = new ArrayList<>();
    private List<ServicePack> servicePacks = new ArrayList<>();

    public EditWorkOrderItemsForm(Stage popupStage, int workOrderId, GarageSystem garageSystem) {
        this.popupStage = popupStage;
        this.workOrderId = workOrderId;
        this.garageSystem = garageSystem;
        errorLabel.getStyleClass().add("error-label");
    }

    public Parent show() {
        content.getStyleClass().add("form-container");
        workOrder = garageSystem.getWorkOrderWithItems(workOrderId).orElse(null);
        servicePacks = garageSystem.getServicePacks();
        refresh();

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        return scrollPane;
    }

    private void refresh() {
        content.getChildren().clear();
        errorLabel.setText("");

        if (workOrder == null) {
            content.getChildren().addAll(
                    new Label(LanguageManager.getString("workorder.edit.error.notFound")),
                    createCloseBtn());
            return;
        }

        Label title = new Label(String.format(
                LanguageManager.getString("workorder.edit.title"), workOrder.getId()));
        title.getStyleClass().add("form-title");

        Label explanation = new Label(LanguageManager.getString("general.form.explanation"));
        explanation.setWrapText(true);
        explanation.setMinWidth(0);
        explanation.getStyleClass().add("form-explanation");

        VBox headerBox = new VBox(title, explanation);
        content.getChildren().add(headerBox);

        if (!workOrder.getStatus().canEdit()) {
            content.getChildren().addAll(
                    new Label(LanguageManager.getString("workorder.edit.locked")),
                    createCloseBtn());
            return;
        }

        //Show all the current service items
        VBox currentServiceRows = new VBox();
        VBox currentServiceBox = new FormFieldContainer(
                LanguageManager.getString("workorder.edit.currentServices"), currentServiceRows, false);

        List<WorkOrderItem> keptItems = workOrder.getItems().stream()
                .filter(item -> !removedItemIds.contains(item.getId()))
                .collect(Collectors.toList());

        boolean isLastItem = keptItems.size() + addedServices.size() <= 1;
        for (WorkOrderItem item : keptItems) {
            currentServiceRows.getChildren().add(createItemRow(item, isLastItem));
        }
        for (ServiceItem addedService : addedServices) {
            currentServiceRows.getChildren().add(createAddedRow(addedService, isLastItem));
        }
        content.getChildren().add(currentServiceBox);

        //Show total price for WorkOrder
        double total = keptItems.stream()
                .mapToDouble(WorkOrderItem::getPriceAtOrder)
                .sum();
        total += addedServices.stream()
                .mapToDouble(ServiceItem::getPrice)
                .sum();
        content.getChildren().add(new Label(String.format(
                LanguageManager.getString("workorder.edit.total"), total)));

        content.getChildren().add(createAddServiceSection(keptItems));
        if (!servicePacks.isEmpty()) {
            content.getChildren().add(createAddPackSection());
        }
        content.getChildren().addAll(errorLabel, createButtonRow());
    }

    private Node createItemRow(WorkOrderItem item, boolean isLastItem) {
        Label nameLabel = new Label(ValueLabels.serviceName(item.getServiceItem().getName()));
        Label priceLabel = new Label(String.format("%.0f kr", item.getPriceAtOrder()));

        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        Button removeBtn = new Button(LanguageManager.getString("workorder.edit.remove"));
        removeBtn.setDisable(isLastItem);
        removeBtn.getStyleClass().add("destroy-btn");
        removeBtn.setOnAction(e -> {
            removedItemIds.add(item.getId());
            refresh();
        });


        HBox row = new HBox(10, nameLabel, priceLabel, removeBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Node createAddedRow(ServiceItem serviceItem, boolean isLastItem) {
        Label nameLabel = new Label(ValueLabels.serviceName(serviceItem.getName()));
        Label priceLabel = new Label(String.format("%.0f kr", serviceItem.getPrice()));

        Button removeBtn = new Button(LanguageManager.getString("workorder.edit.remove"));
        removeBtn.setDisable(isLastItem);
        removeBtn.getStyleClass().add("destroy-btn");
        removeBtn.setOnAction(e -> {
            addedServices.removeIf(added -> added.getId() == serviceItem.getId());
            refresh();
        });

        HBox row = new HBox(10, nameLabel, priceLabel, removeBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Node createAddServiceSection(List<WorkOrderItem> keptItems) {
        List<ServiceItem> availableServices = garageSystem.getServiceItems().stream()
                .filter(serviceItem -> keptItems.stream()
                        .noneMatch(item -> item.getServiceItem().getId() == serviceItem.getId()))
                .filter(serviceItem -> addedServices.stream()
                        .noneMatch(added -> added.getId() == serviceItem.getId()))
                .collect(Collectors.toList());

        ComboBox<ServiceItem> serviceComboBox =
                new ComboBox<>(FXCollections.observableArrayList(availableServices));
        serviceComboBox.setPromptText(LanguageManager.getString("workorder.edit.chooseService"));
        serviceComboBox.setConverter(ComboBoxLabels.serviceItem());

        Button addBtn = new Button(LanguageManager.getString("workorder.edit.add"));
        addBtn.getStyleClass().add("confirm-btn");
        addBtn.disableProperty().bind(serviceComboBox.valueProperty().isNull());
        addBtn.setOnAction(e -> {
            addService(serviceComboBox.getValue());
            refresh();
        });

        HBox comboBoxContainer = new HBox(10, serviceComboBox, addBtn);
        return new FormFieldContainer(
                LanguageManager.getString("workorder.edit.addService"), comboBoxContainer, false);
    }

    private Node createAddPackSection() {
        ComboBox<ServicePack> packComboBox =
                new ComboBox<>(FXCollections.observableArrayList(servicePacks));
        packComboBox.setPromptText(LanguageManager.getString("workorder.pack.prompt"));
        packComboBox.setConverter(ComboBoxLabels.servicePack());

        Button addPackBtn = new Button(LanguageManager.getString("workorder.pack.add"));
        addPackBtn.getStyleClass().add("confirm-btn");
        addPackBtn.disableProperty().bind(packComboBox.valueProperty().isNull());
        addPackBtn.setOnAction(e -> {
            for (ServiceItem item : packComboBox.getValue().getServiceItems()) {
                addService(item);
            }
            refresh();
        });

        HBox comboBoxContainer = new HBox(10, packComboBox, addPackBtn);
        return new FormFieldContainer(
                LanguageManager.getString("workorder.pack.label"), comboBoxContainer, false);
    }

    private void addService(ServiceItem serviceItem) {
        boolean alreadyOnOrder = workOrder.getItems().stream()
                .filter(item -> !removedItemIds.contains(item.getId()))
                .anyMatch(item -> item.getServiceItem().getId() == serviceItem.getId());
        boolean alreadyAdded = addedServices.stream()
                .anyMatch(added -> added.getId() == serviceItem.getId());

        if (alreadyOnOrder || alreadyAdded) {
            return;
        }

        Optional<WorkOrderItem> removedOriginal = workOrder.getItems().stream()
                .filter(item -> removedItemIds.contains(item.getId()))
                .filter(item -> item.getServiceItem().getId() == serviceItem.getId())
                .findFirst();

        if (removedOriginal.isPresent()) {
            removedItemIds.remove(Integer.valueOf(removedOriginal.get().getId()));
        } else {
            addedServices.add(serviceItem);
        }
    }

    private Node createButtonRow() {
        Button saveBtn = new Button(LanguageManager.getString("workorder.edit.save"));
        saveBtn.getStyleClass().add("confirm-btn");
        saveBtn.setDisable(addedServices.isEmpty() && removedItemIds.isEmpty());
        saveBtn.setOnAction(e -> {
            List<Integer> serviceItemIdsToAdd = addedServices.stream()
                    .map(ServiceItem::getId)
                    .collect(Collectors.toList());
            try {
                garageSystem.updateWorkOrderServices(workOrderId, serviceItemIdsToAdd, removedItemIds);
                popupStage.close();
            } catch (AutoCoreException ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        Button cancelBtn = new Button(LanguageManager.getString("workorder.edit.cancel"));
        cancelBtn.setOnAction(e -> popupStage.close());

        return new HBox(10, saveBtn, cancelBtn);
    }

    private Button createCloseBtn() {
        Button closeBtn = new Button(LanguageManager.getString("workorder.edit.close"));
        closeBtn.setOnAction(e -> popupStage.close());
        return closeBtn;
    }
}
