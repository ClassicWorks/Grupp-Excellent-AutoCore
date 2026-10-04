package com.wac.autocore.view;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Invoice;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.components.InvoiceCard;
import com.wac.autocore.view.components.kanban.KanbanColumn;
import com.wac.autocore.view.components.kanban.KanbanGrid;
import com.wac.autocore.view.components.kanban.KanbanGridUtil;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class ShowInvoicesView {
    private final GarageSystem garageSystem;

    public ShowInvoicesView() {
        garageSystem = new GarageSystem();
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
        List<Invoice> unpaidInvoices = garageSystem.getInvoices().stream()
                .filter(invoice -> !invoice.isPaid())
                .collect(Collectors.toList());

        if (unpaidInvoices.isEmpty()) {
            return new Label(LanguageManager.getString("invoices.empty.unpaid"));
        }
        return getInvoiceCardsFromList(unpaidInvoices);
    }

    private Node getPaidList() {
        List<Invoice> paidInvoices = garageSystem.getInvoices().stream()
                .filter(Invoice::isPaid)
                .collect(Collectors.toList());

        if (paidInvoices.isEmpty()) {
            return new Label(LanguageManager.getString("invoices.empty.paid"));
        }
        return getInvoiceCardsFromList(paidInvoices);
    }

    private VBox getInvoiceCardsFromList(List<Invoice> invoices) {
        VBox invoiceCards = new VBox();
        for (Invoice invoice : invoices) {
            invoiceCards.getChildren().add(new InvoiceCard(invoice, i ->
                    ViewManager.getInstance().showInvoiceDetailsPopup(i.getId())
            ));
        }
        invoiceCards.getStyleClass().addAll("card-container");
        return invoiceCards;
    }

    private BorderPane getHeader() {
        BorderPane header = new BorderPane();
        Label title = new Label(LanguageManager.getString("invoices.title"));
        title.getStyleClass().addAll("page-title");

        Button createInvoiceBtn = new Button(LanguageManager.getString("invoices.create"));
        createInvoiceBtn.getStyleClass().addAll("confirm-btn");

        header.setCenter(title);
        header.setRight(createInvoiceBtn);
        createInvoiceBtn.setOnAction(e -> ViewManager.getInstance().showCreateInvoicePopup());
        return header;
    }
}