package com.wac.autocore.view.components;

import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.Vehicle;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BookingDetails extends BorderPane {
    private Label date = new Label();
    private Label registrationNumber = new Label();
    private Label brandModelYear = new Label();
    private Label bookingId = new Label();
    private Hyperlink customerName = new Hyperlink();
    private Label customerInfo = new Label();
    private Hyperlink mechanicName = new Hyperlink();
    private Label description = new Label();
    private Button editBtn = new Button("Edit");
    private Button deleteBtn = new Button("Delete");
    private Button createWorkOrderBtn = new Button("create work order");

    public BookingDetails() {
        HBox topBox = new HBox(10, date, bookingId);

        ImageView vehicleIcon = new IconImageView("resources/imgs/car-solid.png", 40, 40);
        VBox vehicleText = new VBox(registrationNumber, brandModelYear);
        HBox vehicleBox = new HBox(vehicleIcon, vehicleText);

        ImageView customerIcon = new IconImageView("resources/imgs/user-solid.png", 20, 20);
        VBox customerText = new VBox(customerName, customerInfo);
        HBox customerBox = new HBox(customerIcon, customerText);

        ImageView mechanicIcon = new IconImageView("resources/imgs/wrench-solid.png", 20, 20);
        HBox mechanicBox = new HBox(mechanicIcon, mechanicName);

        HBox descriptionBox = new HBox(description);

        HBox actionableButtons = new HBox(20, deleteBtn, editBtn, createWorkOrderBtn);

        this.setTop(topBox);
        this.setCenter(new VBox(vehicleBox, customerBox, mechanicBox, descriptionBox));
        this.setBottom(actionableButtons);
        this.setVisible(false);
    }

    public void populate(Booking booking, Customer customer, Vehicle vehicle, Mechanic mechanic){
        date.setText(String.format("date: %s",booking.getDate().toString()));

        registrationNumber.setText(vehicle.getRegistrationNumber());

        brandModelYear.setText(String.format("%s - %2s, %d",
                vehicle.getBrand(), vehicle.getModel(), vehicle.getYear()));

        bookingId.setText(String.format("Boknings ID: %d", booking.getId()));

        customerName.setText(customer.getName());
        customerName.setOnAction(e ->
                System.out.printf("Should call to ViewManager.getInstance().showCustomers(%d)",
                        customer.getId())
        );

        customerInfo.setText(String.format("mail: %s phone: %2s",
                customer.getEmail(), customer.getPhone())
        );

        if(mechanic != null) {
            mechanicName.setText(mechanic.getName());
            mechanicName.setOnAction(e ->
                    System.out.printf("Should call out to ViewManager.getInstance().showMechanics(%s)",
                            mechanic.getId())
            );
        }else{
            mechanicName.setText("No mechanic assigned");
        }

        description.setText(String.format("Description from customer: %s",booking.getDescription()));

        deleteBtn.setOnAction(e -> System.out.printf("Should call ViewManager.getInstance.deleteBooking(%d)", booking.getId()));
        editBtn.setOnAction(e -> System.out.printf("Should call ViewManager.getInstance.editBooking(%d)", booking.getId()));
        createWorkOrderBtn.setOnAction(e -> System.out.printf("Should call ViewManager.getInstance.createWorkorder(%d)", booking.getId()));

        this.setVisible(true);
    }
}
