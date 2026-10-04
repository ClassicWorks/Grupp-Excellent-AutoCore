package com.wac.autocore.view.components;

import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class CreateBookingForm {
    private static final String SELECTED_STYLE =
            "-fx-background-color: lightblue; -fx-border-color: blue;";

    private static final String UNSELECTED_STYLE =
            "-fx-border-color: blue;";

    private final GarageSystem garageSystem;
    private final Stage popupStage;

    private final ObjectProperty<Vehicle> selectedVehicle =
            new SimpleObjectProperty<>();

    private final VBox vehicleResults = new VBox();
    private final VBox bookingInformation = new VBox();

    private final TextArea descriptionField = new TextArea();
    private ComboBox<Mechanic> mechanicComboBox;


    public CreateBookingForm(Stage popupStage) {
        this.popupStage = popupStage;
        this.garageSystem = new GarageSystem();
    }

    /**
     * User chooses customer and vehicle
     */
    public Parent show() {
        VBox layout = new VBox();

        layout.getChildren().add(createTitle());
        layout.getChildren().add(
                new CustomerSelection(
                        garageSystem.getCustomers(),
                        this::showVehiclesOfCustomer)
        );
        layout.getChildren().add(vehicleResults);
        layout.getChildren().add(createBookingInformation());
        layout.getChildren().add(createActionButtons());

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }


    /**
     * Vehicle is already chosen
     */
    public Parent show(int vehicleId) {
        Vehicle vehicle = garageSystem.getVehicle(vehicleId).orElse(null);

        if (vehicle == null) {
            //TODO dialog window with error message
            return new ScrollPane(new Label(LanguageManager.getString("booking.error.noVehicle")));
        }

        Customer customer = vehicle.getCustomer();
        /*Customer customer = garageSystem
                .getCustomer(vehicle.getCustomerId())
                .orElse(null);*/

        if (customer == null) {
            //TODO dialog window with error message
            return new ScrollPane(new Label(LanguageManager.getString("booking.error.noCustomer")));
        }

        VBox layout = new VBox();

        layout.getChildren().add(createTitle());
        layout.getChildren().add(createCustomerInfo(customer));
        layout.getChildren().add(new VehicleCard(vehicle));
        layout.getChildren().add(createBookingInformation());
        layout.getChildren().add(createActionButtons());

        selectedVehicle.set(vehicle);

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private Label createTitle() {
        return new Label(LanguageManager.getString("booking.form.title"));
    }

    private void showVehiclesOfCustomer(Customer customer) {
        vehicleResults.getChildren().clear();
        selectedVehicle.set(null);

        garageSystem.getCustomersVehicle(customer.getId())
                .forEach(vehicle ->
                        vehicleResults
                                .getChildren()
                                .add(createSelectableVehicleCard(vehicle))
                );
    }

    // =========================================================
    // VEHICLE SELECTION
    // =========================================================
    private Node createSelectableVehicleCard(Vehicle vehicle) {
        Node vehicleCard = new VehicleCard(vehicle);

        vehicleCard.setOnMouseClicked(
                e -> selectVehicle(vehicleCard, vehicle)
        );

        return vehicleCard;
    }


    private void selectVehicle(Node selectedCard, Vehicle vehicle) {
        selectedVehicle.set(vehicle);

        Parent parent = selectedCard.getParent();

        if (parent != null) {
            parent.getChildrenUnmodifiable().forEach(node -> {
                if (node.getStyleClass().contains("vehicle-card")) {
                    node.setStyle(UNSELECTED_STYLE);
                    node.getStyleClass().remove("selected");
                }
            });
        }

        selectedCard.setStyle(SELECTED_STYLE);
        selectedCard.getStyleClass().add("selected");
    }

    // =========================================================
    // BOOKING INFORMATION
    // =========================================================
    private Node createBookingInformation() {
        Label descriptionLabel =
                new Label(LanguageManager.getString("booking.form.description"));

        Label mechanicLabel =
                new Label(LanguageManager.getString("booking.form.mechanicOptional"));

        descriptionField.setPrefRowCount(3);

        mechanicComboBox = createMechanicComboBox();

        bookingInformation.getChildren().clear();
        bookingInformation.getChildren().addAll(
                descriptionLabel,
                descriptionField,
                mechanicLabel,
                mechanicComboBox
        );

        return bookingInformation;
    }

    //TODO only get available mechanics

    private ComboBox<Mechanic> createMechanicComboBox() {
        ObservableList<Mechanic> mechanics =
                FXCollections.observableArrayList(garageSystem.getAvailableMechanics());

        ComboBox<Mechanic> comboBox = new ComboBox<>(mechanics);

        comboBox.setPromptText(LanguageManager.getString("booking.chooseMechanic"));

        return comboBox;
    }

    // =========================================================
    // ACTIONS
    // =========================================================
    private HBox createActionButtons() {
        Button cancelButton =
                new Button(LanguageManager.getString("booking.form.cancel"));

        Button createButton =
                new Button(LanguageManager.getString("booking.form.submit"));

        createButton.disableProperty().bind(
                selectedVehicle.isNull()
                        .or(descriptionField.textProperty().isEmpty())
        );

        createButton.setOnAction(
                e -> createBooking()
        );

        cancelButton.setOnAction(
                e -> popupStage.close()
        );

        return new HBox(
                cancelButton,
                createButton
        );
    }


    private void createBooking() {
        Vehicle vehicle = selectedVehicle.get();

        if (vehicle == null) {
            return;
        }

        Mechanic mechanic = mechanicComboBox.getValue();

        int mechanicId = mechanic != null
                ? mechanic.getId()
                : 0;

        garageSystem.createBooking(
                vehicle.getId(),
                LocalDate.now(),
                descriptionField.getText(),
                mechanicId
        );

        popupStage.close();
    }

    // =========================================================
    // ALREADY SELECTED VEHICLE
    // =========================================================
    private Node createCustomerInfo(Customer customer) {
        ImageView customerIcon =
                new IconImageView("/imgs/user-solid.png", 20, 20);

        Label customerName =
                new Label(customer.getName());

        return new HBox(
                customerIcon,
                customerName
        );
    }

}