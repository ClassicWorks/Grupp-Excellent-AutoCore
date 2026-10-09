package com.wac.autocore.view.components;

import com.wac.autocore.model.Invoice;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.model.WorkOrderStatus;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.ComboBoxLabels;
import com.wac.autocore.util.LanguageManager;
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
        VBox root = new VBox();
        root.getStyleClass().add("form-container");

        Label title = new Label(LanguageManager.getString("invoice.form.title"));
        title.getStyleClass().add("form-title");

        List<Integer> invoicedWorkOrderIds = garageSystem.getInvoices().stream()
                .map(invoice -> invoice.getWorkOrder().getId())
                .collect(Collectors.toList());

        List<WorkOrder> availableWorkOrders = garageSystem.getWorkOrders().stream()
                .filter(workOrder -> workOrder.getStatus() == WorkOrderStatus.COMPLETED)
                .filter(workOrder -> !invoicedWorkOrderIds.contains(workOrder.getId()))
                .collect(Collectors.toList());

        ObservableList<WorkOrder> workOrderItems = FXCollections.observableArrayList(availableWorkOrders);

        ComboBox<WorkOrder> workOrderComboBox = new ComboBox<>(workOrderItems);
        workOrderComboBox.setPromptText(LanguageManager.getString("invoice.form.workorder.prompt"));
        workOrderComboBox.setConverter(ComboBoxLabels.workOrder());

        VBox workOrderBox = new VBox(workOrderComboBox);
        workOrderBox.getStyleClass().add("form-field-container");

        TextField discountCodeField = new TextField();
        discountCodeField.setPromptText(LanguageManager.getString("invoice.form.discount.prompt"));

        VBox discountBox = new VBox(discountCodeField);
        discountBox.getStyleClass().add("form-field-container");

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        Button createBtn = new Button(LanguageManager.getString("invoice.form.submit"));
        createBtn.getStyleClass().add("confirm-btn");

        Button cancelBtn = new Button(LanguageManager.getString("invoice.form.cancel"));
        cancelBtn.getStyleClass().add("cancel-btn");

        createBtn.setOnAction(e -> {
            errorLabel.setText("");

            WorkOrder selectedWorkOrder = workOrderComboBox.getValue();

            if (selectedWorkOrder == null) {
                errorLabel.setText(LanguageManager.getString("invoice.form.error.noWorkOrder"));
                return;
            }

            String discountCode = discountCodeField.getText().trim();

            Invoice invoice = garageSystem.createInvoice(selectedWorkOrder.getId(), discountCode);

            if (invoice == null) {
                errorLabel.setText(LanguageManager.getString("invoice.form.error.failed"));
                return;
            }

            popupStage.close();
        });

        cancelBtn.setOnAction(e -> popupStage.close());

        HBox buttonBox = new HBox(createBtn, cancelBtn);
        buttonBox.getStyleClass().add("btn-container");

        root.getChildren().addAll(
                title,
                workOrderBox,
                discountBox,
                buttonBox,
                errorLabel
        );

        return root;
    }
}