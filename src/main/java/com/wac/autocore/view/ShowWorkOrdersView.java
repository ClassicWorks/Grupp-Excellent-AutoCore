package com.wac.autocore.view;

import com.wac.autocore.controller.Result;
import com.wac.autocore.controller.WorkOrderController;
import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.model.WorkOrderStatus;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.util.ValueLabels;
import com.wac.autocore.view.components.WorkOrderCard;
import com.wac.autocore.view.components.WorkOrderDetails;
import com.wac.autocore.view.components.kanban.KanbanGrid;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class ShowWorkOrdersView {

    private final WorkOrderController workOrderController;
    private final UiResultHandler uiResultHandler = new UiResultHandler();
    private final TabPane tabPane = new TabPane();

    public ShowWorkOrdersView() {
        workOrderController = ViewManager.getInstance().getControllers().workOrders();
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

        tabPane.getTabs().clear();
        for (WorkOrderStatus status : WorkOrderStatus.values()) {
                tabPane.getTabs().add(createTab(status));
        }

        if (selectedIndex >= 0) {
            tabPane.getSelectionModel().select(selectedIndex);
        }
    }

    private Tab createTab(WorkOrderStatus status) {
        VBox detailPanel = new VBox();

        VBox cardList = new VBox();
        cardList.getStyleClass().add("card-container");

        String tabTitle;

        Result<List<WorkOrder>> workOrdersResult = uiResultHandler.handle(workOrderController.getWorkOrdersWithItemsByStatus(status));
        if(workOrdersResult.isSuccess()){
            List<WorkOrder> workOrders = workOrdersResult.value();

            if (workOrders.isEmpty()) {
                cardList.getChildren().add(new Label(LanguageManager.getString("workorders.empty")));
            }

            for (WorkOrder workOrder : workOrders) {
                cardList.getChildren().add(new WorkOrderCard(workOrder, wo ->
                {
                    WorkOrderDetails workOrderDetails = new WorkOrderDetails(wo, this::refreshTabs);
                    detailPanel.getChildren().setAll(workOrderDetails);
                    VBox.setVgrow(workOrderDetails, Priority.ALWAYS);
                }
                ));
            }
            tabTitle = String.format("%s (%d)",
                    ValueLabels.workOrderStatus(status), workOrders.size());
        } else {
            cardList.getChildren().add(new Label(LanguageManager.getString("workorders.errorLoading")));
            tabTitle = String.format("%s (%s)",
                    ValueLabels.workOrderStatus(status), "Error");
        }

        ScrollPane cardScroll = new ScrollPane(cardList);
        cardScroll.setFitToWidth(true);
        cardScroll.setFitToHeight(true);

        KanbanGrid grid = new KanbanGrid(2);
        grid.add(cardScroll, 0, 0);
        grid.add(detailPanel, 1, 0);
        GridPane.setVgrow(cardScroll, Priority.ALWAYS);
        GridPane.setVgrow(detailPanel, Priority.ALWAYS);

        Tab tab = new Tab(tabTitle, grid);
        tab.getStyleClass().addAll(status.name().toLowerCase().replace("_", "-"));
        return tab;
    }

    private BorderPane getHeader() {
        Label title = new Label(LanguageManager.getString("workorders.title"));
        title.getStyleClass().add("page-title");

        Button seeBookingsBtn = new Button(LanguageManager.getString("workorders.seeVehicles"));
        seeBookingsBtn.setOnAction(e -> ViewManager.getInstance().showVehicles());

        BorderPane header = new BorderPane();
        header.setCenter(title);
        header.setRight(seeBookingsBtn);
        header.getStyleClass().add("content-header-container");
        return header;
    }
}