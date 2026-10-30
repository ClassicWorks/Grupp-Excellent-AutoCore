package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class VehicleDetails extends BorderPane {
    private final GarageSystem garageSystem = ViewManager.getInstance().getGarageSystem();
    private final Runnable onSaved;

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


    public VehicleDetails(Vehicle vehicle, Customer vehicleOwner, Runnable onSaved, Consumer<Integer> onDelete) {
        this.vehicle = vehicle;
        this.vehicleOwner = vehicleOwner;
        this.onSaved = onSaved;

        updateCustomerSection();

        errorLabel.getStyleClass().add("error-label");

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

        HBox destructiveButtons = new HBox(createDeleteBtn(onDelete));
        destructiveButtons.getStyleClass().add("btn-container");

        BorderPane actionableButtons = new BorderPane(null, null, destructiveButtons, null, confirmingButtons);

        this.setCenter(mainContent);
        this.setBottom(actionableButtons);
    }

    private void updateCustomerSection() {
        customerName = new CustomerHyperLink(vehicleOwner);

        Label notificationLabel = new Label();
        notificationLabel.getStyleClass().add("error-label");

        ImageViewWithAltText editIcon = new ImageViewWithAltText(
                "/imgs/pen-to-square-solid.png", 20, 20,
                LanguageManager.getString("image.pen-to-square-solid")
        );

        Button editCustomerBtn = new Button(LanguageManager.getString("vehicle.editCustomer"), editIcon);
        editCustomerBtn.getStyleClass().add("confirm-btn");
        editCustomerBtn.setOnAction(e -> showCustomerSelection());

        if (vehicleOwner != null) {
            customerInfo.setText(String.format(
                    LanguageManager.getString("vehicle.customerInfo"),
                    vehicleOwner.getEmail(),
                    vehicleOwner.getPhone()
            ));

            if (vehicle.getCustomer() == null || vehicleOwner.getId() != vehicle.getCustomer().getId()) {
                notificationLabel.setText(LanguageManager.getString("vehicle.ownerChangedNotice"));
            }
        } else {
            notificationLabel.setText(LanguageManager.getString("customer.none.connected"));
            customerInfo.setText("");
        }

        VBox customerText = new VBox(customerName, customerInfo, notificationLabel);
        customerEditableBox.getChildren().setAll(customerText, editCustomerBtn);

        Label customerLabel = new Label(LanguageManager.getString("vehicle.owner"));
        customerLabel.getStyleClass().add("form-field-label");
        customerSection.getChildren().setAll(customerLabel, customerEditableBox);
        customerSection.getStyleClass().add("form-field-container");
    }

    private void showCustomerSelection(){
        CustomerSelection customerSelection = new CustomerSelection(customer -> vehicleOwner = customer
        );

        Button changeCustomerBtn = new Button(LanguageManager.getString("vehicle.setCustomer"));
        changeCustomerBtn.getStyleClass().add("confirm-btn");

        customerEditableBox.getChildren().setAll(
                new VBox(
                        customerSelection,
                        changeCustomerBtn)
        );

        changeCustomerBtn.setOnAction(e -> updateCustomerSection());
    }

    private HBox createVehicleEditForm() {
        ImageViewWithAltText vehicleIcon =
                new ImageViewWithAltText("/imgs/car-solid.png", 40, 40,
                        LanguageManager.getString("image.car-solid"));

        vehicleIdLabel.setText(String.format(LanguageManager.getString("vehicle.id"), vehicle.getId()));
        vehicleIdLabel.getStyleClass().add("details-id");

        registrationNumber.setText(vehicle.getRegistrationNumber());
        brandField.setText(vehicle.getBrand());
        modelField.setText(vehicle.getModel());
        yearField.setText(Integer.toString(vehicle.getYear()));

        Label explanation = new Label(LanguageManager.getString("general.form.explanation"));
        explanation.setWrapText(true);
        explanation.setMinWidth(0);
        explanation.getStyleClass().add("form-explanation");

        VBox registrationBox = new FormFieldContainer(
                LanguageManager.getString("vehicle.regNumber"), registrationNumber, true);
        VBox brandBox = new FormFieldContainer(LanguageManager.getString("vehicle.brand"), brandField, true);
        VBox modelBox = new FormFieldContainer(LanguageManager.getString("vehicle.model"), modelField, true);
        VBox yearBox = new FormFieldContainer(LanguageManager.getString("vehicle.year"), yearField, true);

        VBox vehicleFields = new VBox(
                vehicleIdLabel,
                explanation,
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
        /*vehicleFields.setMinWidth(0);
        vehicleFields.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(vehicleFields, Priority.ALWAYS);
        vehicleBox.setMaxWidth(Double.MAX_VALUE);*/

        vehicleBox.setAlignment(Pos.CENTER_LEFT);

        return vehicleBox;
    }

    private Button createBookVehicleBtn() {
        Button bookVehicleBtn = new Button(LanguageManager.getString("vehicle.book"));
        bookVehicleBtn.getStyleClass().add("confirm-btn");
        bookVehicleBtn.setOnAction(e ->
                ViewManager.getInstance().showCreateBooking(vehicle.getId())
        );
        return bookVehicleBtn;
    }

    private Button createSaveBtn() {
        Button saveBtn = new Button(LanguageManager.getString("vehicle.save"));
        saveBtn.getStyleClass().add("confirm-btn");

        saveBtn.disableProperty().bind(
                registrationNumber.textProperty().isEmpty()
                        .or(modelField.textProperty().isEmpty())
                        .or(yearField.textProperty().isEmpty())
        );

        saveBtn.setOnAction(e -> {
            errorLabel.setText("");
            errorLabel.getStyleClass().remove("success");

            if (!isInteger(yearField.getText().trim())) {
                errorLabel.setText(LanguageManager.getString("vehicle.error.yearNumber"));
                return;
            }

            Vehicle updated = garageSystem.updateVehicle(
                    vehicle.getId(),
                    registrationNumber.getText().trim(),
                    brandField.getText().trim(),
                    modelField.getText().trim(),
                    Integer.parseInt(yearField.getText().trim()),
                    vehicleOwner == null ? null : vehicleOwner.getId()
            );

            if (updated == null) {
                errorLabel.setText(LanguageManager.getString("vehicle.error.saveFailed"));
                return;
            }

            vehicle = updated;
            updateCustomerSection();

            errorLabel.getStyleClass().add("success");
            errorLabel.setText(LanguageManager.getString("vehicle.saved"));
            onSaved.run();
        });

        return saveBtn;
    }

    private Button createDeleteBtn(Consumer<Integer> onDelete) {
        Button deleteBtn = new Button(LanguageManager.getString("vehicle.delete"));
        deleteBtn.getStyleClass().add("destroy-btn");
        deleteBtn.setOnAction(e -> onDelete.accept(vehicle.getId()));
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
