package com.wac.autocore.view;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.*;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.components.kanban.KanbanColumn;
import com.wac.autocore.view.components.kanban.KanbanGrid;
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

        //Could be added later
        //Node filterView = getFilter();

        //Create grid with 3 columns
        KanbanGrid kanbanGrid = new KanbanGrid(3);

        //Create columns
        VBox incomingOrdersView =
                new KanbanColumn(
                        "Incoming",
                        getIncomingList(),
                        "ready"
                );

        VBox inProgressOrdersView =
                new KanbanColumn(
                        "In progress",
                        getInProgressList(),
                        "in-progress"
                );

        VBox completedOrdersView =
                new KanbanColumn(
                        "Completed",
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
            return new Label("No completed and unpaid work orders found");
        }

        return getWorkOrderCardsFromList(completedWorkOrders);
    }

    private Node getInProgressList() {
        List<WorkOrder> completedWorkOrders = garageSystem.getWorkOrdersWithItems().stream()
                .filter(wo -> wo.getStatus().equalsIgnoreCase("IN_PROGRESS"))
                .collect(Collectors.toList());
        if (completedWorkOrders.isEmpty()) {
            return new Label("No in progress work orders found");
        }

        return getWorkOrderCardsFromList(completedWorkOrders);
    }

    private Node getIncomingList() {
        List<WorkOrder> completedWorkOrders = garageSystem.getWorkOrdersWithItems().stream()
                .filter(wo -> wo.getStatus().equalsIgnoreCase("CREATED"))
                .collect(Collectors.toList());
        if (completedWorkOrders.isEmpty()) {
            return new Label("No incoming work orders found");
        }

        return getWorkOrderCardsFromList(completedWorkOrders);
    }

    private VBox getWorkOrderCardsFromList(List<WorkOrder> workOrders) {
        VBox workOrderCards = new VBox();
        for(WorkOrder workOrder : workOrders) {
            //Check if necessary objects exists
            Mechanic mechanic = workOrder.getMechanic();
            if(mechanic == null){
                workOrderCards.getChildren().add(new Label("Mechanic not found"));
                continue;
            }
            Booking booking = workOrder.getBooking();
            if(booking == null){
                workOrderCards.getChildren().add(new Label("Booking not found"));
                continue;
            }
            Vehicle vehicle = booking.getVehicle();
            if(vehicle == null){
                workOrderCards.getChildren().add(new Label("Vehicle not found"));
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
        Label title = new Label("Work Orders");
        title.getStyleClass().add("page-title");

        Button createWorkOrderBtn = new Button("See bookings");

        BorderPane header = new BorderPane();
        header.getStyleClass().add("content-header-container");
        header.setCenter(title);
        header.setRight(createWorkOrderBtn);
        createWorkOrderBtn.setOnAction(e -> ViewManager.getInstance().showBookings());
        return header;
    }
}
