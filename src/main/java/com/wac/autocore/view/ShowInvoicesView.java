package com.wac.autocore.view;

import com.wac.autocore.controller.InvoiceController;
import com.wac.autocore.controller.Result;
import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Invoice;
import com.wac.autocore.security.Permission;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.components.InvoiceCard;
import com.wac.autocore.view.components.kanban.KanbanColumn;
import com.wac.autocore.view.components.kanban.KanbanGrid;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class ShowInvoicesView {
    private final InvoiceController invoiceController;

    public ShowInvoicesView() {
        invoiceController = ViewManager.getInstance().getControllers().invoices();
    }

    public Parent show() {
        BorderPane root = new BorderPane();
        Node header = getHeader();
        header.getStyleClass().addAll("content-header-container");

        GridPane kanbanGrid = new KanbanGrid(2);

        VBox unpaidView =
                new KanbanColumn(LanguageManager.getString("invoices.unpaid"), getUnpaidList(), "unhandled");

        VBox paidView =
                new KanbanColumn(LanguageManager.getString("invoices.paid"), getPaidList(), "completed");

        kanbanGrid.add(unpaidView, 0, 0);
        kanbanGrid.add(paidView, 1, 0);

        GridPane.setVgrow(unpaidView, Priority.ALWAYS);
        GridPane.setVgrow(paidView, Priority.ALWAYS);

        root.setTop(header);
        root.setCenter(kanbanGrid);
        return root;
    }

    private Node getUnpaidList() {
        Result<List<Invoice>> result = invoiceController.getUnpaidInvoices();
        if (!result.isSuccess()) {
            return getErrorLabel(result.error());
        }
        List<Invoice> unpaidInvoices = result.value();

        if (unpaidInvoices.isEmpty()) {
            return new Label(LanguageManager.getString("invoices.empty.unpaid"));
        }
        return getInvoiceCardsFromList(unpaidInvoices);
    }

    private Node getPaidList() {
        Result<List<Invoice>> result = invoiceController.getPaidInvoices();
        if (!result.isSuccess()) {
            return getErrorLabel(result.error());
        }
        List<Invoice> paidInvoices = result.value();

        if (paidInvoices.isEmpty()) {
            return new Label(LanguageManager.getString("invoices.empty.paid"));
        }
        return getInvoiceCardsFromList(paidInvoices);
    }

    private Label getErrorLabel(String message) {
        Label label = new Label(message);
        label.getStyleClass().add("error-label");
        label.setWrapText(true);
        return label;
    }

    private VBox getInvoiceCardsFromList(List<Invoice> invoices) {
        VBox invoiceCards = new VBox();
        for (Invoice invoice : invoices) {
            InvoiceCard invoiceCard = new InvoiceCard(invoice, i ->
                    ViewManager.getInstance().showInvoiceDetailsPopup(i.getId())
            );
            invoiceCard.getStyleClass().add("clickable");
            invoiceCards.getChildren().add(invoiceCard);
        }
        invoiceCards.getStyleClass().addAll("card-container");
        return invoiceCards;
    }

    private BorderPane getHeader() {
        BorderPane header = new BorderPane();
        Label title = new Label(LanguageManager.getString("invoices.title"));
        title.getStyleClass().addAll("page-title");
        header.setCenter(title);

        boolean canManageInvoices = ViewManager.getInstance().getAccessGuard().has(Permission.MANAGE_INVOICES);
        if (canManageInvoices) {
            Button createInvoiceBtn = new Button(LanguageManager.getString("invoices.create"));
            createInvoiceBtn.getStyleClass().addAll("confirm-btn");
            createInvoiceBtn.setOnAction(e -> ViewManager.getInstance().showCreateInvoicePopup());
            header.setRight(createInvoiceBtn);
        }
        return header;
    }
}