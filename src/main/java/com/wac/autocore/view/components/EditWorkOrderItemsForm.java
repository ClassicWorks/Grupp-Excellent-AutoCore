package com.wac.autocore.view.components;

import com.wac.autocore.model.ServiceItem;
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

    public EditWorkOrderItemsForm(Stage popupStage, int workOrderId, GarageSystem garageSystem) {
        this.popupStage = popupStage;
        this.workOrderId = workOrderId;
        this.garageSystem = garageSystem;
        errorLabel.getStyleClass().add("error-label");
    }

    public Parent show() {
        content.getStyleClass().add("form-container");
        workOrder = garageSystem.getWorkOrderWithItems(workOrderId).orElse(null);
        refresh();

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
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
        content.getChildren().add(title);

        if (!workOrder.getStatus().equals("CREATED")) {
            content.getChildren().addAll(
                    new Label(LanguageManager.getString("workorder.edit.locked")),
                    createCloseBtn());
            return;
        }

        //Show all the current service items
        Label currentServicesLabel = new Label(LanguageManager.getString("workorder.edit.currentServices"));
        currentServicesLabel.getStyleClass().add("form-field-label");

        VBox currentServiceBox = new VBox(currentServicesLabel);
        currentServiceBox.getStyleClass().add("form-field-container");

        List<WorkOrderItem> keptItems = workOrder.getItems().stream()
                .filter(item -> !removedItemIds.contains(item.getId()))
                .collect(Collectors.toList());

        boolean isLastItem = keptItems.size() + addedServices.size() <= 1;
        for (WorkOrderItem item : keptItems) {
            currentServiceBox.getChildren().add(createItemRow(item, isLastItem));
        }
        for (ServiceItem addedService : addedServices) {
            currentServiceBox.getChildren().add(createAddedRow(addedService, isLastItem));
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

        content.getChildren().addAll(
                createAddServiceSection(keptItems),
                errorLabel,
                createButtonRow());
    }

    private Node createItemRow(WorkOrderItem item, boolean isLastItem) {
        Label nameLabel = new Label(ValueLabels.serviceName(item.getServiceItem().getName()));
        Label priceLabel = new Label(String.format("%.0f kr", item.getPriceAtOrder()));

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
            ServiceItem selected = serviceComboBox.getValue();
            Optional<WorkOrderItem> removedOriginal = workOrder.getItems().stream()
                    .filter(item -> removedItemIds.contains(item.getId()))
                    .filter(item -> item.getServiceItem().getId() == selected.getId())
                    .findFirst();
            if (removedOriginal.isPresent()) {
                removedItemIds.remove(Integer.valueOf(removedOriginal.get().getId()));
            } else {
                addedServices.add(selected);
            }
            refresh();
        });

        Label addLabel = new Label(LanguageManager.getString("workorder.edit.addService"));
        addLabel.getStyleClass().add("form-field-label");

        HBox comboBoxContainer = new HBox(10, serviceComboBox, addBtn);
        VBox container = new VBox(addLabel, comboBoxContainer);
        container.getStyleClass().add("form-field-container");
        return container;
    }

    private Node createButtonRow() {
        Button saveBtn = new Button(LanguageManager.getString("workorder.edit.save"));
        saveBtn.getStyleClass().add("confirm-btn");
        saveBtn.setDisable(addedServices.isEmpty() && removedItemIds.isEmpty());
        saveBtn.setOnAction(e -> {
            List<Integer> serviceItemIdsToAdd = addedServices.stream()
                    .map(ServiceItem::getId)
                    .collect(Collectors.toList());
            WorkOrder updated = garageSystem.updateWorkOrderServices(
                    workOrderId, serviceItemIdsToAdd, removedItemIds);
            if (updated == null) {
                errorLabel.setText(LanguageManager.getString("workorder.edit.error.save"));
                return;
            }
            popupStage.close();
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