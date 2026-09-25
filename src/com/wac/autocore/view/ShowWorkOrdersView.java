package com.wac.autocore.view;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.*;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.components.KanbanGridUtil;
import com.wac.autocore.view.components.WorkOrderCardWithActionBtns;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Optional;
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
        GridPane kanbanGrid = KanbanGridUtil.getKanbanGrid(3);

        //Create columns
        VBox incomingOrdersView =
                KanbanGridUtil.getScrollableColumnWithTitle("Incoming", getIncomingList());

        VBox inProgressOrdersView =
                KanbanGridUtil.getScrollableColumnWithTitle("In progress", getInProgressList());

        VBox completedOrdersView =
                KanbanGridUtil.getScrollableColumnWithTitle("Completed", getCompletedList());

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

    private VBox getWorkOrderCardsFromList(List<WorkOrder> workOrders) {
        VBox workOrderCards = new VBox();
        for(WorkOrder workOrder : workOrders) {
            //Check if necessary objects exists
            Optional<Mechanic> optionalMechanic = garageSystem.getMechanic(workOrder.getMechanicId());
            if(!optionalMechanic.isPresent()){
                workOrderCards.getChildren().add(new Label("Mechanic not found"));
                continue;
            }
            Mechanic mechanic = optionalMechanic.get();
            Optional<Booking> optionalBooking = garageSystem.getBooking(workOrder.getBookingId());
            if(!optionalBooking.isPresent()){
                workOrderCards.getChildren().add(new Label("Booking not found"));
                continue;
            }
            Booking booking = optionalBooking.get();
            Optional<Vehicle> optionalVehicle = garageSystem.getVehicle(booking.getVehicleId());
            if(!optionalVehicle.isPresent()){
                workOrderCards.getChildren().add(new Label("Vehicle not found"));
                continue;
            }
            Vehicle vehicle = optionalVehicle.get();

            workOrderCards.getChildren().add(new WorkOrderCardWithActionBtns(workOrder, booking, vehicle,mechanic));
        }
        return workOrderCards;
    }

    private BorderPane getHeader() {
        BorderPane header = new BorderPane();
        Label title = new Label("Work Orders");
        Button createWorkOrderBtn = new Button("See bookings");
        header.setCenter(title);
        header.setRight(createWorkOrderBtn);
        createWorkOrderBtn.setOnAction(e -> ViewManager.getInstance().showBookings());
        return header;
    }
}
