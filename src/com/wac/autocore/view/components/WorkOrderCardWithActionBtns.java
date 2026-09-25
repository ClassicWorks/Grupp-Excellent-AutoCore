package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.service.GarageSystem;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class WorkOrderCardWithActionBtns extends HBox {
    private GarageSystem garageSystem = new GarageSystem();
    private WorkOrder workOrder;
    private Booking booking;
    private Vehicle vehicle;
    private Mechanic mechanic;
    public WorkOrderCardWithActionBtns(WorkOrder workOrder, Booking booking, Vehicle vehicle, Mechanic mechanic) {
        this.workOrder = workOrder;
        this.booking = booking;
        this.vehicle = vehicle;
        this.mechanic = mechanic;

        //Get basic card
        WorkOrderCard card = new WorkOrderCard(workOrder, booking, vehicle, mechanic);

        //Add OrderActionBtn to card
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.BASELINE_RIGHT);
        buttonBox.getChildren().add(createOrderActionBtn());

        card.getChildren().add(buttonBox);

        //Add Edit and Delete Btns to card
        ImageView editIcon = new ImageView("resources/imgs/pen-to-square-solid.png");
        editIcon.setFitHeight(30);
        editIcon.setFitWidth(30);
        ImageView deleteIcon = new ImageView("resources/imgs/trash-solid.png");
        deleteIcon.setFitHeight(30);
        deleteIcon.setFitWidth(30);

        Button editBookingBtn = new Button("redigera", editIcon);
        editBookingBtn.getStyleClass().addAll("edit-card-btn");
        editBookingBtn.setOnAction(e ->
                System.out.printf("Calling ViewManager.getInstance().editWorkOrder(%d)\n",workOrder.getId())
        );

        Button deleteBookingBtn = new Button("Radera", deleteIcon);
        deleteBookingBtn.getStyleClass().addAll("delete-card-btn");
        deleteBookingBtn.setOnAction(e ->
                System.out.printf("Calling ViewManager.getInstance().deleteWorkOrder(%d)\n",workOrder.getId())
        );
        VBox actionableBox = new VBox(editBookingBtn, deleteBookingBtn);

        this.setMaxWidth(Double.MAX_VALUE);

        this.getChildren().addAll(card, actionableBox);
    }

    private Node createOrderActionBtn(){
        switch(workOrder.getStatus().toUpperCase()){
            case "CREATED":
                Button startWorkBtn = new Button("Start work");
                startWorkBtn.setOnAction(e -> {
                    garageSystem.startWorkOrder(workOrder.getId());
                    ViewManager.getInstance().showWorkOrders();
                });
                startWorkBtn.getStyleClass().add("start-work-btn");
                return startWorkBtn;
            case "IN_PROGRESS":
                Button completeWorkBtn = new Button("Complete work");
                completeWorkBtn.setOnAction(e -> {
                    garageSystem.completeWorkOrder(workOrder.getId());
                    ViewManager.getInstance().showWorkOrders();
                });
                completeWorkBtn.getStyleClass().add("complete-work-btn");
                return completeWorkBtn;
        }
        return new Label("No action required");
    }
}
