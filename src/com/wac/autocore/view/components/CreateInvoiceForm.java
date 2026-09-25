package com.wac.autocore.view.components;

import com.wac.autocore.data.Database;
import com.wac.autocore.model.Invoice;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.service.GarageSystem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class CreateInvoiceForm {

    private final GarageSystem garageSystem = new GarageSystem();
    private final Stage popupStage;

    public CreateInvoiceForm(Stage popupStage) {
        this.popupStage = popupStage;
    }

    public Parent show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label title = new Label("Create invoice");

        List<Integer> invoicedWorkOrderIds = Database.getInvoices().stream()
                .map(Invoice::getWorkOrderId)
                .collect(Collectors.toList());

        List<WorkOrder> availableWorkOrders = Database.getWorkOrders().stream()
                .filter(workOrder -> workOrder.getStatus().equalsIgnoreCase("COMPLETED"))
                .filter(workOrder -> !invoicedWorkOrderIds.contains(workOrder.getId()))
                .collect(Collectors.toList());

        ObservableList<WorkOrder> workOrderItems = FXCollections.observableArrayList(availableWorkOrders);
        ComboBox<WorkOrder> workOrderComboBox = new ComboBox<>(workOrderItems);
        workOrderComboBox.setPromptText("Choose work order");

        TextField discountCodeField = new TextField();
        discountCodeField.setPromptText("Discount code (optional)");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button createBtn = new Button("Create invoice");
        Button cancelBtn = new Button("Cancel");

        createBtn.setOnAction(e -> {
            errorLabel.setText("");

            WorkOrder selectedWorkOrder = workOrderComboBox.getValue();

            if (selectedWorkOrder == null) {
                errorLabel.setText("You must select a work order.");
                return;
            }

            String discountCode = discountCodeField.getText().trim();

            Invoice invoice = garageSystem.createInvoice(selectedWorkOrder.getId(), discountCode);

            if (invoice == null) {
                errorLabel.setText("Could not create invoice.");
                return;
            }

            popupStage.close();
        });

        cancelBtn.setOnAction(e -> popupStage.close());

        HBox buttonBox = new HBox(10, createBtn, cancelBtn);

        root.getChildren().addAll(title, workOrderComboBox, discountCodeField, buttonBox, errorLabel);

        return root;
    }
}