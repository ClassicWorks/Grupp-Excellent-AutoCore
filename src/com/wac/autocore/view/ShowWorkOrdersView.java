package com.wac.autocore.view;

import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.components.WorkOrderCard;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
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

        GridPane kanbanView = new GridPane();
        ColumnConstraints incomingColumn = new ColumnConstraints();
        incomingColumn.setPercentWidth(33.33);
        ColumnConstraints inProgressColumn = new ColumnConstraints();
        inProgressColumn.setPercentWidth(33.33);
        ColumnConstraints completedColumn = new ColumnConstraints();
        completedColumn.setPercentWidth(33.33);
        kanbanView.getColumnConstraints().addAll(incomingColumn, inProgressColumn, completedColumn);

        ScrollPane incomingView = new ScrollPane(getIncomingList());
        ScrollPane inProgressView = new ScrollPane(getInProgressList());
        ScrollPane completedView = new ScrollPane(getCompletedList());

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
        return new VBox();
    }

    private Node getIncomingList() {

        return new VBox();
    }

    private HBox getWorkOrderCardsFromList(List<WorkOrder> workOrders) {
        HBox workOrderCards = new HBox();
        for(WorkOrder workOrder : workOrders) {
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
            //TODO continue
            workOrderCards.getChildren().add(new WorkOrderCard());
        }
        return workOrderCards;
    }

    private Node getHeader() {
        HBox header = new HBox();
        return header;
    }
}
