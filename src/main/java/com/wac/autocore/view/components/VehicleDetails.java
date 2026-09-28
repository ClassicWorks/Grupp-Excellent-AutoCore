package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.GarageSystem;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class VehicleDetails extends BorderPane {
    private final GarageSystem garageSystem = new GarageSystem();

    private Vehicle vehicle;
    private Customer vehicleOwner;

    private Label vehicleIdLabel = new Label();
    private TextField registrationNumber = new TextField();
    private TextField brandField = new TextField();
    private TextField modelField = new TextField();
    private TextField yearField = new TextField();
    private Hyperlink customerName = new Hyperlink();
    private Label customerInfo = new Label();
    private HBox customerSection = new HBox();
    private Label errorLabel = new Label();


    public VehicleDetails(Vehicle vehicle, Customer vehicleOwner) {
        this.vehicle = vehicle;
        this.vehicleOwner = vehicleOwner;

        Label vehicleOwnerLabel = new Label("Owner");

        VBox mainContent = new VBox(
                createVehicleEditForm(),
                vehicleOwnerLabel,
                customerSection,
                errorLabel
        );

        createCustomerSection();

        HBox actionableButtons = new HBox(20,
                createDeleteBtn(),
                createSaveBtn(),
                createBookVehicleBtn());

        this.setCenter(mainContent);
        this.setBottom(actionableButtons);
    }

    private void createCustomerSection() {
        IconImageView customerIcon = new IconImageView(
                "/imgs/user-solid.png", 20, 20
        );

        Label isChangedNotification = new Label();
        VBox customerText = new VBox(customerName, customerInfo, isChangedNotification);

        IconImageView editIcon = new IconImageView(
                "/imgs/pen-to-square-solid.png", 20, 20
        );

        Button editCustomerBtn = new Button("Edit customer", editIcon);
        editCustomerBtn.setOnAction(e -> showCustomerSelection());

        customerSection.getChildren().setAll(customerIcon, customerText, editCustomerBtn);

        if (vehicleOwner != null) {
            customerName.setText(vehicleOwner.getName());
            customerName.setOnAction(e ->
                    System.out.println("Should call on ViewManager.getInstance().showCustomers(customer.getId())")
            );

            customerInfo.setText(String.format(
                    "mail: %s phone: %s",
                    vehicleOwner.getEmail(),
                    vehicleOwner.getPhone()
            ));

            if(vehicleOwner.getId() != vehicle.getCustomerId()){
                isChangedNotification.setText("Vehicle needs to be saved to set new vehicle owner");
            }
        } else {
            customerName.setText("No customer connected");
            customerName.setOnAction(null);
            customerInfo.setText("");
        }
    }

    private void showCustomerSelection(){
        CustomerSelection customerSelection = new CustomerSelection(
                garageSystem.getCustomers(),
                customer -> vehicleOwner = customer
        );

        Button changeCustomerBtn = new Button("Set customer");

        customerSection.getChildren().setAll(
                new VBox(
                    customerSelection,
                    changeCustomerBtn)
        );

        changeCustomerBtn.setOnAction(e -> createCustomerSection());
    }

    private HBox createVehicleEditForm() {
        IconImageView vehicleIcon = new IconImageView("/imgs/car-solid.png", 40, 40);

        vehicleIdLabel.setText(String.format("Fordons ID: %d", vehicle.getId()));

        registrationNumber.setText(vehicle.getRegistrationNumber());

        brandField.setText(vehicle.getBrand());
        modelField.setText(vehicle.getModel());
        yearField.setText(Integer.toString(vehicle.getYear()));

        VBox vehicleFields = new VBox(10,
                vehicleIdLabel,
                new Label("Registration number"),
                registrationNumber,
                new Label("Brand"),
                brandField,
                new Label("Model"),
                modelField,
                new Label("Year"),
                yearField
        );
        HBox vehicleBox = new HBox(vehicleIcon, vehicleFields);
        vehicleBox.setAlignment(Pos.CENTER_LEFT);
        return vehicleBox;
    }

    private Button createBookVehicleBtn() {
        Button bookVehicleBtn = new Button("Book vehicle");
        bookVehicleBtn.setOnAction(e ->
                ViewManager.getInstance().showCreateBooking(vehicle.getId())
        );
        return bookVehicleBtn;
    }

    private Button createSaveBtn() {
        Button saveBtn = new Button("Save changes");

        saveBtn.disableProperty().bind(
        registrationNumber.textProperty().isEmpty()
                .or(modelField.textProperty().isEmpty())
                .or(yearField.textProperty().isEmpty())
                );

        //TODO savefunction
        saveBtn.setOnAction(e -> {
            if(!isInteger(yearField.getText())){
                errorLabel.setText("Year must be a number");
                return;
            }
            //TODO check fields
            Vehicle newVehicle = new Vehicle(
                    vehicle.getId(),
                    registrationNumber.getText(),
                    brandField.getText(),
                    modelField.getText(),
                    Integer.parseInt(yearField.getText()),
                    vehicleOwner.getId()
            );
            System.out.printf("Should call ViewManager.getInstance.saveVehicle(%d, %s)", vehicle.getId(), newVehicle);

        });

        return saveBtn;
    }

    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private Button createDeleteBtn() {
        Button deleteBtn = new Button("Delete");

        deleteBtn.setOnAction(e ->
                System.out.printf("Should call ViewManager.getInstance.confirmDelete(vehicle, garagesystem.deleteVehicle(%d)",
                        vehicle.getId())
        );
        return deleteBtn;
    }
}
