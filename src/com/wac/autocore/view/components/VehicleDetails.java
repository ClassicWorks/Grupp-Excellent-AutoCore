package com.wac.autocore.view.components;

import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class VehicleDetails extends BorderPane {
    private Label id = new Label();
    private Label registrationNumber = new Label();
    private Label brandModelYear = new Label();
    private Hyperlink customerName = new Hyperlink();
    private Label customerInfo = new Label();
    private Button editBtn = new Button("Edit");
    private Button deleteBtn = new Button("Delete");
    private Button bookVehicleBtn = new Button("Book vehicle");

    public VehicleDetails() {
        HBox topBox = new HBox(10, id);

        ImageView vehicleIcon = new ImageView(new Image("resources/imgs/car-solid.png"));
        vehicleIcon.setFitHeight(40);
        vehicleIcon.setFitWidth(40);
        VBox vehicleText = new VBox(registrationNumber, brandModelYear);
        HBox vehicleBox = new HBox(vehicleIcon, vehicleText);

        ImageView customerIcon = new ImageView("resources/imgs/user-solid.png");
        customerIcon.setFitWidth(20);
        customerIcon.setFitHeight(20);
        VBox customerText = new VBox(customerName, customerInfo);
        HBox customerBox = new HBox(customerIcon, customerText);

        HBox actionableButtons = new HBox(20, deleteBtn, editBtn, bookVehicleBtn);

        this.setTop(topBox);
        this.setCenter(new VBox(vehicleBox, customerBox));
        this.setBottom(actionableButtons);
        this.setVisible(false);
    }

    public void populate(Vehicle vehicle, Customer customer) {
        id.setText(String.format("Fordons ID: %d", vehicle.getId()));

        registrationNumber.setText(vehicle.getRegistrationNumber());

        brandModelYear.setText(String.format("%s - %2s, %d",
                vehicle.getBrand(), vehicle.getModel(), vehicle.getYear()));

        if (customer != null) {
            customerName.setText(customer.getName());
            customerName.setOnAction(e ->
                    System.out.printf("Should call to ViewManager.getInstance().showCustomers(%d)",
                            customer.getId())
            );

            customerInfo.setText(String.format("mail: %s phone: %2s",
                    customer.getEmail(), customer.getPhone())
            );
        } else {
            customerName.setText("No customer connected");
            customerName.setOnAction(null);
            customerInfo.setText("");
        }

        deleteBtn.setOnAction(e -> System.out.printf("Should call ViewManager.getInstance.deleteVehicle(%d)", vehicle.getId()));
        editBtn.setOnAction(e -> System.out.printf("Should call ViewManager.getInstance.editVehicle(%d)", vehicle.getId()));
        bookVehicleBtn.setOnAction(e -> System.out.printf("Should call ViewManager.getInstance.createBooking(%d)", vehicle.getId()));

        this.setVisible(true);
    }
}
