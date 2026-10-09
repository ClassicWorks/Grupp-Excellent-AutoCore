package com.wac.autocore.view.components;

import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.model.WorkOrderItem;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.ComboBoxLabels;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.util.ValueLabels;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class EditWorkOrderItemsForm {

    private final GarageSystem garageSystem = new GarageSystem();
    private final Stage popupStage;
    private final int workOrderId;

    private final VBox content = new VBox(10);
    private final Label errorLabel = new Label();

    public EditWorkOrderItemsForm(Stage popupStage, int workOrderId) {
        this.popupStage = popupStage;
        this.workOrderId = workOrderId;
        errorLabel.setStyle("-fx-text-fill: red;");
    }

    public Parent show() {
        content.setPadding(new Insets(20));
        refresh();

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private void refresh() {
        content.getChildren().clear();
        errorLabel.setText("");

        Optional<WorkOrder> optionalWorkOrder = garageSystem.getWorkOrderWithItems(workOrderId);

        if (!optionalWorkOrder.isPresent()) {
            content.getChildren().addAll(
                    new Label(LanguageManager.getString("workorder.edit.error.notFound")),
                    createCloseBtn());
            return;
        }
        WorkOrder workOrder = optionalWorkOrder.get();

        Label title = new Label(String.format(
                LanguageManager.getString("workorder.edit.title"), workOrder.getId()));
        title.getStyleClass().add("form-heading");
        content.getChildren().add(title);

        if (!workOrder.getStatus().canEditServices()) {
            content.getChildren().addAll(
                    new Label(LanguageManager.getString("workorder.edit.locked")),
                    createCloseBtn());
            return;
        }

        content.getChildren().add(new Label(LanguageManager.getString("workorder.edit.currentServices")));

        boolean isLastItem = workOrder.getItems().size() <= 1;
        for (WorkOrderItem item : workOrder.getItems()) {
            content.getChildren().add(createItemRow(item, isLastItem));
        }

        double total = workOrder.getItems().stream()
                .mapToDouble(WorkOrderItem::getPriceAtOrder)
                .sum();
        content.getChildren().add(new Label(String.format(
                LanguageManager.getString("workorder.edit.total"), total)));

        content.getChildren().addAll(
                createAddServiceSection(workOrder),
                errorLabel,
                createCloseBtn());
    }

    private Node createItemRow(WorkOrderItem item, boolean isLastItem) {
        Label nameLabel = new Label(ValueLabels.serviceName(item.getServiceItem().getName()));
        Label priceLabel = new Label(String.format("%.0f kr", item.getPriceAtOrder()));

        Button removeBtn = new Button(LanguageManager.getString("workorder.edit.remove"));
        removeBtn.setDisable(isLastItem);
        removeBtn.setOnAction(e -> {
            WorkOrder updated = garageSystem.removeItemFromWorkOrder(workOrderId, item.getId());
            if (updated == null) {
                errorLabel.setText(LanguageManager.getString("workorder.edit.error.remove"));
                return;
            }
            refresh();
        });

        HBox row = new HBox(10, nameLabel, priceLabel, removeBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Node createAddServiceSection(WorkOrder workOrder) {
        List<ServiceItem> availableServices = garageSystem.getServiceItems().stream()
                .filter(serviceItem -> !workOrder.hasService(serviceItem.getId()))
                .collect(Collectors.toList());

        ComboBox<ServiceItem> serviceComboBox =
                new ComboBox<>(FXCollections.observableArrayList(availableServices));
        serviceComboBox.setPromptText(LanguageManager.getString("workorder.edit.chooseService"));
        serviceComboBox.setConverter(ComboBoxLabels.serviceItem());

        Button addBtn = new Button(LanguageManager.getString("workorder.edit.add"));
        addBtn.disableProperty().bind(serviceComboBox.valueProperty().isNull());
        addBtn.setOnAction(e -> {
            ServiceItem selected = serviceComboBox.getValue();
            WorkOrder updated = garageSystem.addServiceToWorkOrder(workOrderId, selected.getId());
            if (updated == null) {
                errorLabel.setText(LanguageManager.getString("workorder.edit.error.add"));
                return;
            }
            refresh();
        });

        Label addLabel = new Label(LanguageManager.getString("workorder.edit.addService"));
        return new VBox(5, addLabel, new HBox(10, serviceComboBox, addBtn));
    }

    private Button createCloseBtn() {
        Button closeBtn = new Button(LanguageManager.getString("workorder.edit.close"));
        closeBtn.setOnAction(e -> popupStage.close());
        return closeBtn;
    }
}