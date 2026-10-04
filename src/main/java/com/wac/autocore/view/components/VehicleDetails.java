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
    private HBox customerEditableBox = new HBox();
    private VBox customerSection = new VBox();
    private Label errorLabel = new Label();


    public VehicleDetails(Vehicle vehicle, Customer vehicleOwner) {
        this.vehicle = vehicle;
        this.vehicleOwner = vehicleOwner;

        getCustomerSection();
        customerSection.getStyleClass().add("form-field-container");

        VBox mainContent = new VBox(
                createVehicleEditForm(),
                customerSection,
                errorLabel
        );
        mainContent.getStyleClass().add("details-container");

        HBox confirmingButtons = new HBox(
                createSaveBtn(),
                createBookVehicleBtn()
        );
        confirmingButtons.getStyleClass().add("btn-container");

        HBox destructiveButtons = new HBox(createDeleteBtn());
        destructiveButtons.getStyleClass().add("btn-container");

        BorderPane actionableButtons = new BorderPane(null, null, confirmingButtons, null, destructiveButtons);

        this.setCenter(mainContent);
        this.setBottom(actionableButtons);
    }

    private void getCustomerSection() {
        IconImageView customerIcon = new IconImageView(
                "/imgs/user-solid.png", 20, 20
        );

        Label customerLabel = new Label("Owner");
        customerLabel.getStyleClass().add("form-field-label");

        Label notificationLabel = new Label();
        notificationLabel.getStyleClass().add("error-label");
        VBox customerText = new VBox(customerName, customerInfo, notificationLabel);

        IconImageView editIcon = new IconImageView(
                "/imgs/pen-to-square-solid.png", 20, 20
        );

        Button editCustomerBtn = new Button("Edit customer", editIcon);
        editCustomerBtn.getStyleClass().add("confirm-btn");
        editCustomerBtn.setOnAction(e -> showCustomerSelection());

        customerEditableBox.getChildren().setAll(customerIcon, customerText, editCustomerBtn);


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

            if(vehicleOwner.getId() != vehicle.getCustomer().getId()){
                notificationLabel.setText("Vehicle needs to be saved to set new vehicle owner");
            }
        } else {
            customerName.setText("No customer connected");
            customerName.setOnAction(null);
            notificationLabel.setText("Customer needs to be set");
            customerInfo.setText("");
        }

        customerSection.getChildren().setAll(customerLabel, customerEditableBox);

    }

    private void showCustomerSelection(){
        CustomerSelection customerSelection = new CustomerSelection(
                garageSystem.getCustomers(),
                customer -> vehicleOwner = customer
        );

        Button changeCustomerBtn = new Button("Set customer");
        changeCustomerBtn.getStyleClass().add("confirm-btn");

        customerEditableBox.getChildren().setAll(
                new VBox(
                    customerSelection,
                    changeCustomerBtn)
        );

        changeCustomerBtn.setOnAction(e -> getCustomerSection());
    }

    private HBox createVehicleEditForm() {
        IconImageView vehicleIcon =
                new IconImageView("/imgs/car-solid.png", 40, 40);

        vehicleIdLabel.setText(String.format("Fordons ID: %d", vehicle.getId()));
        vehicleIdLabel.getStyleClass().add("details-id");

        registrationNumber.setText(vehicle.getRegistrationNumber());
        brandField.setText(vehicle.getBrand());
        modelField.setText(vehicle.getModel());
        yearField.setText(Integer.toString(vehicle.getYear()));

        Label registrationLabel = new Label("Registration number");
        registrationLabel.getStyleClass().add("form-field-label");

        VBox registrationBox =
                new VBox(registrationLabel, registrationNumber);
        registrationBox.getStyleClass().add("form-field-container");


        Label brandLabel = new Label("Brand");
        brandLabel.getStyleClass().add("form-field-label");

        VBox brandBox =
                new VBox(brandLabel, brandField);
        brandBox.getStyleClass().add("form-field-container");


        Label modelLabel = new Label("Model");
        modelLabel.getStyleClass().add("form-field-label");

        VBox modelBox =
                new VBox(modelLabel, modelField);
        modelBox.getStyleClass().add("form-field-container");


        Label yearLabel = new Label("Year");
        yearLabel.getStyleClass().add("form-field-label");

        VBox yearBox =
                new VBox(yearLabel, yearField);
        yearBox.getStyleClass().add("form-field-container");


        VBox vehicleFields = new VBox(
                vehicleIdLabel,
                registrationBox,
                brandBox,
                modelBox,
                yearBox
        );
        vehicleFields.getStyleClass().add("form-container");


        HBox vehicleBox = new HBox(
                vehicleIcon,
                vehicleFields
        );

        vehicleBox.setAlignment(Pos.CENTER_LEFT);

        return vehicleBox;
    }

    private Button createBookVehicleBtn() {
        Button bookVehicleBtn = new Button("Book vehicle");
        bookVehicleBtn.getStyleClass().add("confirm-btn");
        bookVehicleBtn.setOnAction(e ->
                ViewManager.getInstance().showCreateBooking(vehicle.getId())
        );
        return bookVehicleBtn;
    }

    private Button createSaveBtn() {
        Button saveBtn = new Button("Save changes");
        saveBtn.getStyleClass().add("confirm-btn");

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
                    vehicleOwner
            );
            System.out.printf("Should call ViewManager.getInstance.saveVehicle(%d, %s)", vehicle.getId(), newVehicle);

        });

        return saveBtn;
    }

    private Button createDeleteBtn() {
        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("destroy-btn");
        deleteBtn.setOnAction(e ->
                System.out.printf("Should call ViewManager.getInstance.confirmDelete(vehicle, garagesystem.deleteVehicle(%d)",
                        vehicle.getId())
        );
        return deleteBtn;
    }

    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
