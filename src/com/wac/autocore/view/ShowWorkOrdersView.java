package com.wac.autocore.view;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.*;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.components.WorkOrderCard;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

        //If needed
        //Node filterView = getFilter();

        GridPane kanbanGrid = new GridPane();
        ColumnConstraints incomingColumn = new ColumnConstraints();
        incomingColumn.setPercentWidth(33.33);
        ColumnConstraints inProgressColumn = new ColumnConstraints();
        inProgressColumn.setPercentWidth(33.33);
        ColumnConstraints completedColumn = new ColumnConstraints();
        completedColumn.setPercentWidth(33.33);
        kanbanGrid.getColumnConstraints().addAll(incomingColumn, inProgressColumn, completedColumn);


        ScrollPane incomingOrdersScroll = new ScrollPane(getIncomingList());
        ScrollPane inProgressOrdersScroll = new ScrollPane(getInProgressList());
        ScrollPane completedOrdersScroll = new ScrollPane(getCompletedList());

        Label incomingOrdersLabel = getColumnHeader("Incoming");
        Label inProgressOrdersLabel = getColumnHeader("In progress");
        Label completedOrdersLabel = getColumnHeader("Completed");

        VBox incomingOrdersView = new VBox(
                incomingOrdersLabel,
                incomingOrdersScroll
        );
        VBox.setVgrow(incomingOrdersScroll, Priority.ALWAYS);

        VBox inProgressOrdersView = new VBox(
                inProgressOrdersLabel,
                inProgressOrdersScroll
        );
        VBox.setVgrow(inProgressOrdersScroll, Priority.ALWAYS);

        VBox completedOrdersView = new VBox(
                completedOrdersLabel,
                completedOrdersScroll
        );
        VBox.setVgrow(completedOrdersScroll, Priority.ALWAYS);

        kanbanGrid.add(incomingOrdersView, 0, 0);
        kanbanGrid.add(inProgressOrdersView, 1, 0);
        kanbanGrid.add(completedOrdersView, 2, 0);
        GridPane.setVgrow(incomingOrdersView, Priority.ALWAYS);
        GridPane.setVgrow(inProgressOrdersView, Priority.ALWAYS);
        GridPane.setVgrow(completedOrdersView, Priority.ALWAYS);

        root.setTop(header);
        root.setCenter(kanbanGrid);
        return root;
    }

    private Label getColumnHeader(String incomingOrders) {
        Label columnHeader = new Label(incomingOrders);
        columnHeader.getStyleClass().add("column-heder");
        columnHeader.setAlignment(Pos.CENTER);
        columnHeader.setMaxWidth(Double.MAX_VALUE);
        return columnHeader;
    }

    private Node getCompletedList() {
        List<WorkOrder> completedWorkOrders = garageSystem.getWorkOrders().stream()
                .filter(wo -> wo.getStatus().equalsIgnoreCase("COMPLETED"))
                .collect(Collectors.toList());
        if (completedWorkOrders.isEmpty()) {
            return new Label("No completed and unpaid work orders found");
        }

        return getWorkOrderCardsFromList(completedWorkOrders);
    }

    private Node getInProgressList() {
        List<WorkOrder> completedWorkOrders = garageSystem.getWorkOrders().stream()
                .filter(wo -> wo.getStatus().equalsIgnoreCase("IN_PROGRESS"))
                .collect(Collectors.toList());
        if (completedWorkOrders.isEmpty()) {
            return new Label("No in progress work orders found");
        }

        return getWorkOrderCardsFromList(completedWorkOrders);
    }

    private Node getIncomingList() {
        List<WorkOrder> completedWorkOrders = garageSystem.getWorkOrders().stream()
                .filter(wo -> wo.getStatus().equalsIgnoreCase("CREATED"))
                .collect(Collectors.toList());
        if (completedWorkOrders.isEmpty()) {
            return new Label("No incoming work orders found");
        }

        return getWorkOrderCardsFromList(completedWorkOrders);
    }

    private HBox getWorkOrderCardsFromList(List<WorkOrder> workOrders) {
        HBox workOrderCards = new HBox();
        for(WorkOrder workOrder : workOrders) {
            Mechanic mechanic = garageSystem.getMechanic(workOrder.getMechanicId()).orElse(null);
            if(mechanic == null){
                workOrderCards.getChildren().add(new Label("Mechanic not found"));
                continue;
            }
            Booking booking = garageSystem.getBooking(workOrder.getBookingId()).orElse(null);
            if(booking == null){
                workOrderCards.getChildren().add(new Label("Booking not found"));
                continue;
            }
            Vehicle vehicle = garageSystem.getVehicle(booking.getVehicleId()).orElse(null);
            if(vehicle == null){
                workOrderCards.getChildren().add(new Label("Vehicle not found"));
                continue;
            }
            Customer customer = garageSystem.getCustomer(vehicle.getCustomerId()).orElse(null);
            if(customer == null){
                workOrderCards.getChildren().add(new Label("Customer not found"));
                continue;
            }

            //TODO continue
            workOrderCards.getChildren().add(new WorkOrderCard(workOrder, booking, vehicle,mechanic));
        }
        return workOrderCards;
    }

    private Node getHeader() {
        BorderPane header = new BorderPane();
        Label title = new Label("Work Orders");
        Button createWorkOrderBtn = new Button("See bookings");
        header.setCenter(title);
        header.setRight(createWorkOrderBtn);
        createWorkOrderBtn.setOnAction(e -> ViewManager.getInstance().showBookings());
        return header;
    }
}
