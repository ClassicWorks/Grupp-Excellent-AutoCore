package com.wac.autocore.view.components;

import com.wac.autocore.model.Invoice;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.model.WorkOrderItem;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.util.ValueLabels;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class InvoiceDetailsPopup {

    private final GarageSystem garageSystem = new GarageSystem();
    private final Stage popupStage;
    private final int invoiceId;

    public InvoiceDetailsPopup(Stage popupStage, int invoiceId) {
        this.popupStage = popupStage;
        this.invoiceId = invoiceId;
    }

    public Parent show() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Optional<Invoice> optionalInvoice = garageSystem.getInvoice(invoiceId);
        if (!optionalInvoice.isPresent()) {
            content.getChildren().addAll(
                    new Label(LanguageManager.getString("invoice.details.error.notFound")),
                    createCloseBtn());
            return wrapInScrollPane(content);
        }
        Invoice invoice = optionalInvoice.get();

        Optional<WorkOrder> optionalWorkOrder =
                garageSystem.getWorkOrderWithItems(invoice.getWorkOrder().getId());
        if (!optionalWorkOrder.isPresent()) {
            content.getChildren().addAll(
                    new Label(LanguageManager.getString("invoice.details.error.workOrderNotFound")),
                    createCloseBtn());
            return wrapInScrollPane(content);
        }
        WorkOrder workOrder = optionalWorkOrder.get();

        Label title = new Label(String.format(LanguageManager.getString("invoice.id"), invoice.getId()));
        title.getStyleClass().add("form-heading");

        Label dateLabel = new Label(String.format(
                LanguageManager.getString("invoice.details.date"), invoice.getInvoiceDate()));

        Label statusLabel = new Label(invoice.isPaid()
                ? LanguageManager.getString("invoice.status.paid")
                : LanguageManager.getString("invoice.status.unpaid"));

        GridPane linesGrid = new GridPane();
        linesGrid.setHgap(20);
        linesGrid.setVgap(5);

        int row = 0;
        for (WorkOrderItem item : workOrder.getItems()) {
            linesGrid.add(new Label(ValueLabels.serviceName(item.getServiceItem().getName())), 0, row);
            linesGrid.add(new Label(formatAmount(item.getPriceAtOrder())), 1, row);
            row++;
        }

        linesGrid.add(new Separator(), 0, row, 2, 1);
        row++;

        linesGrid.add(new Label(LanguageManager.getString("invoice.details.amount")), 0, row);
        linesGrid.add(new Label(formatAmount(invoice.getAmount())), 1, row);
        row++;

        if (invoice.getDiscount() > 0) {
            linesGrid.add(new Label(LanguageManager.getString("invoice.details.discount")),0, row);
            linesGrid.add(new Label("-" + formatAmount(invoice.getDiscount())), 1, row);
            row++;
        }

        linesGrid.add(new Label(LanguageManager.getString("invoice.details.total")), 0, row);
        linesGrid.add(new Label(formatAmount(invoice.getTotalAmount())), 1, row);

        content.getChildren().addAll(
                title,
                dateLabel,
                statusLabel,
                new Label(LanguageManager.getString("invoice.details.lines")),
                linesGrid,
                createCloseBtn());

        return wrapInScrollPane(content);
    }

    private String formatAmount(double amount) {
        return String.format(LanguageManager.getString("invoice.details.amountFormat"), amount);
    }

    private Parent wrapInScrollPane(VBox content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private Button createCloseBtn() {
        Button closeBtn = new Button(LanguageManager.getString("invoice.details.close"));
        closeBtn.setOnAction(e -> popupStage.close());
        return closeBtn;
    }
}