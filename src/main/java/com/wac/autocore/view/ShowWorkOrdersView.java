package com.wac.autocore.view;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.*;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.components.kanban.KanbanColumn;
import com.wac.autocore.view.components.kanban.KanbanGrid;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.components.WorkOrderCard;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.List;
import java.util.stream.Collectors;

public class ShowWorkOrdersView {
    private final GarageSystem garageSystem;

    public ShowWorkOrdersView() {
        garageSystem = new GarageSystem();
    }

    public Parent show() {
        BorderPane root = new BorderPane();
        Node header = getHeader();
        header.getStyleClass().add("content-header-container");

        //Could be added later
        //Node filterView = getFilter();

        //Create grid with 3 columns
        KanbanGrid kanbanGrid = new KanbanGrid(3);

        //Create columns
        VBox incomingOrdersView =
                new KanbanColumn(
                        LanguageManager.getString("workorders.column.incoming"),
                        getIncomingList(),
                        "ready"
                );

        VBox inProgressOrdersView =
                new KanbanColumn(
                        LanguageManager.getString("workorders.column.inProgress"),
                        getInProgressList(),
                        "in-progress"
                );

        VBox completedOrdersView =
                new KanbanColumn(
                        LanguageManager.getString("workorders.column.completed"),
                        getCompletedList(),
                        "completed"
                );

        kanbanGrid.add(incomingOrdersView, 0, 0);
        kanbanGrid.add(inProgressOrdersView, 1, 0);
        kanbanGrid.add(completedOrdersView, 2, 0);

        //Make columns able to grow
        GridPane.setVgrow(incomingOrdersView, Priority.ALWAYS);
        GridPane.setVgrow(inProgressOrdersView, Priority.ALWAYS);
        GridPane.setVgrow(completedOrdersView, Priority.ALWAYS);

        root.setTop(header);
        root.setCenter(kanbanGrid);
        return root;
    }

    //TODO move logic to garageSystem
    private Node getCompletedList() {
        List<WorkOrder> completedWorkOrders = garageSystem.getWorkOrdersWithItems().stream()
                .filter(wo -> wo.getStatus().equalsIgnoreCase("COMPLETED"))
                .collect(Collectors.toList());
        if (completedWorkOrders.isEmpty()) {
            return new Label(LanguageManager.getString("workorders.empty.completed"));
        }

        return getWorkOrderCardsFromList(completedWorkOrders);
    }

    private Node getInProgressList() {
        List<WorkOrder> completedWorkOrders = garageSystem.getWorkOrdersWithItems().stream()
                .filter(wo -> wo.getStatus().equalsIgnoreCase("IN_PROGRESS"))
                .collect(Collectors.toList());
        if (completedWorkOrders.isEmpty()) {
            return new Label(LanguageManager.getString("workorders.empty.inProgress"));
        }

        return getWorkOrderCardsFromList(completedWorkOrders);
    }

    private Node getIncomingList() {
        List<WorkOrder> completedWorkOrders = garageSystem.getWorkOrdersWithItems().stream()
                .filter(wo -> wo.getStatus().equalsIgnoreCase("CREATED"))
                .collect(Collectors.toList());
        if (completedWorkOrders.isEmpty()) {
            return new Label(LanguageManager.getString("workorders.empty.incoming"));
        }

        return getWorkOrderCardsFromList(completedWorkOrders);
    }

    private VBox getWorkOrderCardsFromList(List<WorkOrder> workOrders) {
        VBox workOrderCards = new VBox();
        for(WorkOrder workOrder : workOrders) {
            //Check if necessary objects exists
            Mechanic mechanic = workOrder.getMechanic();
            if(mechanic == null){
                workOrderCards.getChildren().add(new Label(LanguageManager.getString("workorders.error.mechanicNotFound")));
                continue;
            }
            Booking booking = workOrder.getBooking();
            if(booking == null){
                workOrderCards.getChildren().add(new Label(LanguageManager.getString("workorders.error.bookingNotFound")));
                continue;
            }
            Vehicle vehicle = booking.getVehicle();
            if(vehicle == null){
                workOrderCards.getChildren().add(new Label(LanguageManager.getString("workorders.error.vehicleNotFound")));
                continue;
            }

            List<WorkOrderItem> items = workOrder.getItems();

            workOrderCards.getChildren().add(new WorkOrderCard(workOrder, booking, vehicle,mechanic, items));
        }
        workOrderCards.getStyleClass().add("card-container");
        VBox.setVgrow(workOrderCards, Priority.ALWAYS);
        workOrderCards.setMaxHeight(Double.MAX_VALUE);
        return workOrderCards;
    }

    private BorderPane getHeader() {
        Label title = new Label(LanguageManager.getString("workorders.title"));
        title.getStyleClass().add("page-title");

        Button createWorkOrderBtn = new Button(LanguageManager.getString("workorders.seeBookings"));
        createWorkOrderBtn.setOnAction(e -> ViewManager.getInstance().showBookings());

        BorderPane header = new BorderPane();
        header.setCenter(title);
        header.setRight(createWorkOrderBtn);
        return header;
    }
}