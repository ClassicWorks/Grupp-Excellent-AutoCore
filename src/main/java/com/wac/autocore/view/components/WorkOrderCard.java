package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.*;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.util.ValueLabels;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;

public class WorkOrderCard extends VBox {
    GarageSystem garageSystem = new GarageSystem();
    private WorkOrder workOrder;
    private Booking booking;
    private Vehicle vehicle;
    private Mechanic mechanic;

    public WorkOrderCard(WorkOrder workOrder, Booking booking, Vehicle vehicle, Mechanic mechanic, List<WorkOrderItem> workOrderItems) {
        this.workOrder = workOrder;
        this.booking = booking;
        this.vehicle = vehicle;
        this.mechanic = mechanic;

        HBox statusInfo = getStatusInfo();

        HBox vehicleInfo = getVehicleInfo();

        //mechanic info
        Hyperlink mechanicLink = new MechanicHyperLink(mechanic);

        Text description = new Text(booking.getDescriptionFor(LanguageManager.getCurrentLanguage()));

        VBox serviceItemBox = getServiceItemBox(workOrderItems);

        //Add OrderActionBtn to card
        HBox buttonBox = new HBox();
        buttonBox.getStyleClass().add("btn-container");
        buttonBox.setAlignment(Pos.BASELINE_RIGHT);
        buttonBox.getChildren().add(createOrderActionBtn());


        this.getStyleClass().add("card");
        this.setMaxWidth(Double.MAX_VALUE);

        this.getChildren().addAll(statusInfo, vehicleInfo, mechanicLink, description, serviceItemBox, buttonBox);
    }

    private VBox getServiceItemBox(List<WorkOrderItem> workOrderItems) {
        Label serviceItemsLabel = new Label(LanguageManager.getString("workorder.serviceItems"));
        if(workOrderItems.isEmpty()){
            Label error = new Label(LanguageManager.getString("workorder.serviceItems.empty"));
            return new VBox(serviceItemsLabel, error);
        }
        GridPane itemGrid = new GridPane();
        int totalTime = 0;
        for(int i = 0; i < workOrderItems.size(); i++){
            WorkOrderItem item = workOrderItems.get(i);
            ServiceItem serviceItem = item.getServiceItem();
            if(serviceItem == null) {
                System.out.println("service item not found: id %d");
                Label error = new Label(String.format(LanguageManager.getString("workorder.serviceItems.itemMissing"), item.getId()));
                itemGrid.add(error, 0, i);
            }else {
                Label itemName = new Label(ValueLabels.serviceName(item.getServiceItem().getName()));

                totalTime += item.getServiceItem().getEstimatedMinutes();
                ImageViewWithAltText clockIcon = new ImageViewWithAltText(
                        "imgs/clock-solid.png", 20, 20,
                        LanguageManager.getString("image.clock-solid")
                );
                Label timeLabel = new Label(Integer.toString(item.getServiceItem().getEstimatedMinutes()) + " min",
                        clockIcon);
                timeLabel.setAlignment(Pos.CENTER_RIGHT);

                itemGrid.add(itemName, 0, i);
                itemGrid.add(timeLabel, 1, i);
            }
        }

        itemGrid.add(new Label(LanguageManager.getString("workorder.totalTime")), 0, workOrderItems.size());
        itemGrid.add(new Label(Integer.toString(totalTime) + " min"), 1, workOrderItems.size());

        itemGrid.add(new Label(LanguageManager.getString("workorder.totalPrice")), 0, workOrderItems.size() + 1);
        itemGrid.add((new Label(String.format("%.0f kr", workOrder.getTotalPrice()))), 1, workOrderItems.size() + 1);

        VBox serviceItemBox = new VBox(serviceItemsLabel, itemGrid);
        serviceItemBox.getStyleClass().add("card-info-box");
        itemGrid.getStyleClass().add("grid");
        return serviceItemBox;
    }

    private HBox getVehicleInfo() {
        ImageViewWithAltText vehicleIcon = new ImageViewWithAltText("/imgs/car-solid.png", 40, 40,
                LanguageManager.getString("image.car-solid"));

        Label registrationNumber = new Label(vehicle.getRegistrationNumber());

        Label brandModelYear = new Label(String.format("%s %s %d",
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear())
        );

        VBox vehicleText = new VBox(registrationNumber, brandModelYear);
        HBox vehicleInfo = new HBox(vehicleIcon, vehicleText);
        vehicleInfo.getStyleClass().add("card-info-box");
        return vehicleInfo;
    }

    private HBox getStatusInfo() {
        HBox statusInfo;
        Label idsLabel = new Label(String.format(LanguageManager.getString("workorder.ids"),
                workOrder.getId(), booking.getId()) + " ");
        Label statusLabel = createStatusLabel();

        statusInfo = new HBox(idsLabel, statusLabel);
        statusInfo.getStyleClass().add("card-info-box");
        return statusInfo;
    }

    public Label createStatusLabel() {
        Label statusLabel;
        switch (workOrder.getStatus().toUpperCase()) {
            case "CREATED":
                getStyleClass().add("created");

                statusLabel = new Label(LanguageManager.getString("workorder.status.incoming"));
                statusLabel.getStyleClass().add("incoming");
                break;

            case "IN_PROGRESS":
                getStyleClass().add("in-progress");

                statusLabel = new Label(LanguageManager.getString("workorder.status.inProgress"));
                statusLabel.getStyleClass().add("in-progress");
                break;

            case "COMPLETED":
                getStyleClass().add("completed");

                statusLabel = new Label(LanguageManager.getString("workorder.status.completed"));
                statusLabel.getStyleClass().add("completed");
                break;

            default:
                statusLabel = new Label(LanguageManager.getString("workorder.status.unknown"));
                break;
        }

        statusLabel.getStyleClass().addAll("status-label");
        return statusLabel;
    }

    //TODO Move connection to GarageSystem to ShowWorkOrderView
    private Node createOrderActionBtn(){
        switch(workOrder.getStatus().toUpperCase()){
            case "CREATED":
                Button editServiceBtn = new Button(LanguageManager.getString("workorder.editServices"));
                editServiceBtn.setOnAction(e ->
                        ViewManager.getInstance().showEditWorkOrderItemsPopUp(workOrder.getId())
                );
                editServiceBtn.getStyleClass().addAll("confirm-btn", "card-action-btn");

                Button startWorkBtn = new Button(LanguageManager.getString("workorder.start"));
                startWorkBtn.setOnAction(e -> {
                    garageSystem.startWorkOrder(workOrder.getId());
                    ViewManager.getInstance().showWorkOrders();
                });
                startWorkBtn.getStyleClass().addAll("confirm-btn", "card-action-btn");

                return new HBox(10, editServiceBtn, startWorkBtn);
            case "IN_PROGRESS":
                Button completeWorkBtn = new Button(LanguageManager.getString("workorder.complete"));
                completeWorkBtn.setOnAction(e -> {
                    garageSystem.completeWorkOrder(workOrder.getId());
                    ViewManager.getInstance().showWorkOrders();
                });
                completeWorkBtn.getStyleClass().addAll("confirm-btn", "card-action-btn");
                return completeWorkBtn;
        }
        return new Label(LanguageManager.getString("workorder.noAction"));
    }
}