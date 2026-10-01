package com.wac.autocore.view.components;

import com.wac.autocore.model.*;
import com.wac.autocore.service.GarageSystem;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

@Deprecated
public class WorkOrderCardWithActionBtns extends HBox {
    private GarageSystem garageSystem = new GarageSystem();
    private WorkOrder workOrder;
    private Booking booking;
    private Vehicle vehicle;
    private Mechanic mechanic;
    public WorkOrderCardWithActionBtns(WorkOrder workOrder, Booking booking, Vehicle vehicle, Mechanic mechanic, List<WorkOrderItem> workOrderItems) {
        this.workOrder = workOrder;
        this.booking = booking;
        this.vehicle = vehicle;
        this.mechanic = mechanic;

        //Get basic card
        WorkOrderCard card = new WorkOrderCard(workOrder, booking, vehicle, mechanic, workOrderItems);

        //Add Edit and Delete Btns to card
        ImageView editIcon = new ImageView("/imgs/pen-to-square-solid.png");
        editIcon.setFitHeight(30);
        editIcon.setFitWidth(30);
        ImageView deleteIcon = new ImageView("/imgs/trash-solid.png");
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


}
