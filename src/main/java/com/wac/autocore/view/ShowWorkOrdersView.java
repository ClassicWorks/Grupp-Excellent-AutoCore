package com.wac.autocore.view;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.*;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.ValueLabels;
import com.wac.autocore.view.components.WorkOrderDetails;
import com.wac.autocore.view.components.kanban.KanbanColumn;
import com.wac.autocore.view.components.kanban.KanbanGrid;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.components.WorkOrderCard;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.stream.Collectors;

public class ShowWorkOrdersView {

    private final GarageSystem garageSystem;
    private final TabPane tabPane = new TabPane();

    public ShowWorkOrdersView() {
        garageSystem = new GarageSystem();
    }

    public Parent show() {
        BorderPane root = new BorderPane();
        root.setTop(getHeader());

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        refreshTabs();

        root.setCenter(tabPane);
        return root;
    }

    public void refreshTabs() {
        int selectedIndex = tabPane.getSelectionModel().getSelectedIndex();

        List<WorkOrder> allWorkOrders = garageSystem.getWorkOrdersWithItems();

        tabPane.getTabs().clear();
        for (WorkOrderStatus status : WorkOrderStatus.values()) {
            List<WorkOrder> workOrders = allWorkOrders.stream()
                    .filter(workOrder -> workOrder.getStatus() == status)
                    .collect(Collectors.toList());

            tabPane.getTabs().add(createTab(status, workOrders));
        }

        if (selectedIndex >= 0) {
            tabPane.getSelectionModel().select(selectedIndex);
        }
    }

    private Tab createTab(WorkOrderStatus status, List<WorkOrder> workOrders) {
        VBox detailPanel = new VBox();

        VBox cardList = new VBox();
        cardList.getStyleClass().add("card-container");

        if (workOrders.isEmpty()) {
            cardList.getChildren().add(new Label(LanguageManager.getString("workorders.empty")));
        }

        for (WorkOrder workOrder : workOrders) {
            cardList.getChildren().add(new WorkOrderCard(workOrder, wo ->
                    detailPanel.getChildren().setAll(new WorkOrderDetails(wo, this::refreshTabs))));
        }

        ScrollPane cardScroll = new ScrollPane(cardList);
        cardScroll.setFitToWidth(true);

        KanbanGrid grid = new KanbanGrid(2);
        grid.add(cardScroll, 0, 0);
        grid.add(detailPanel, 1, 0);
        GridPane.setVgrow(cardScroll, Priority.ALWAYS);
        GridPane.setVgrow(detailPanel, Priority.ALWAYS);

        String tabTitle = String.format("%s (%d)",
                ValueLabels.workOrderStatus(status), workOrders.size());

        return new Tab (tabTitle, grid);
    }

    private BorderPane getHeader() {
        Label title = new Label(LanguageManager.getString("workorders.title"));
        title.getStyleClass().add("page-title");

        Button seeBookingsBtn = new Button(LanguageManager.getString("workorders.seeBookings"));
        seeBookingsBtn.setOnAction(e -> ViewManager.getInstance().showBookings());

        BorderPane header = new BorderPane();
        header.setCenter(title);
        header.setRight(seeBookingsBtn);
        header.getStyleClass().add("content-header-container");
        return header;
    }
}