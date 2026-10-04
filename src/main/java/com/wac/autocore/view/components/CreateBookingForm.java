package com.wac.autocore.view.components;

import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.StylingUtil;
import com.wac.autocore.util.ComboBoxLabels;
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
        Label title = createTitle();

        CustomerSelection customerSelection = new CustomerSelection(
                garageSystem.getCustomers(),
                this::showVehiclesOfCustomer);
        customerSelection.getStyleClass().addAll("form-field-container");

        vehicleResults.getStyleClass().addAll("card-container", "compact");

        HBox actionButtons = createActionButtons();
        actionButtons.getStyleClass().add("btn-container");

        layout.getChildren().add(
                customerSelection
        );
        layout.getChildren().addAll(
                createTitle(),
                vehicleResults,
                getDescriptionBox(),
                getMechanicBox(),
                actionButtons
        );

        layout.getStyleClass().add("form-container");

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        return scrollPane;
    }


    /**
     * Vehicle is already chosen
     */
    public Parent show(int vehicleId) {
        Vehicle vehicle = garageSystem.getVehicle(vehicleId).orElse(null);

        if (vehicle == null) {
            //TODO dialog window with error message
            Label errorLabel = new Label(LanguageManager.getString("booking.error.noVehicle"));
            errorLabel.getStyleClass().add("error-label");
            return new ScrollPane(errorLabel);
        }

        Customer customer = vehicle.getCustomer();

        if (customer == null) {
            //TODO dialog window with error message
            Label errorLabel = new Label(LanguageManager.getString("booking.error.noCustomer"));
            errorLabel.getStyleClass().add("error-label");
            return new ScrollPane(errorLabel);
        }

        VehicleCard vehicleCard = new VehicleCard(vehicle, customer);
        vehicleCard.getStyleClass().add("compact");

        HBox actionButtons = createActionButtons();

        VBox layout = new VBox();

        layout.getChildren().addAll(
                createTitle(),
                vehicleCard,
                getDescriptionBox(),
                getMechanicBox(),
                actionButtons
        );

        selectedVehicle.set(vehicle);

        layout.getStyleClass().add("form-container");

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        return scrollPane;
    }

    private Label createTitle() {
        Label title = new Label(LanguageManager.getString("booking.form.title"));
        title.getStyleClass().add("form-title");
        return title;
    }

    // =========================================================
    // VEHICLE SELECTION
    // =========================================================
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

    private Node createSelectableVehicleCard(Vehicle vehicle) {
        Node vehicleCard = new VehicleCard(vehicle);

        vehicleCard.setOnMouseClicked(e -> {
            selectVehicle(vehicleCard, vehicle);
            StylingUtil.setSelected(vehicleCard, "card");
        });

        vehicleCard.getStyleClass().add("clickable");

        return vehicleCard;
    }


    private void selectVehicle(Node selectedCard, Vehicle vehicle) {
        selectedVehicle.set(vehicle);

        Parent parent = selectedCard.getParent();

        if (parent != null) {
            parent.getChildrenUnmodifiable().forEach(node -> {
                if (node.getStyleClass().contains("card")) {
                    node.getStyleClass().remove("is-selected");
                }
            });
        }
        selectedCard.getStyleClass().add("is-selected");
    }

    // =========================================================
    // BOOKING INFORMATION
    // =========================================================

    private VBox getMechanicBox() {
        Label mechanicLabel = new Label(LanguageManager.getString("booking.form.mechanicOptional"));
        mechanicLabel.getStyleClass().add("form-field-label");

        mechanicComboBox = createMechanicComboBox();

        VBox mechanicBox = new VBox(mechanicLabel, mechanicComboBox);
        mechanicBox.getStyleClass().add("form-field-container");
        return mechanicBox;
    }

    private VBox getDescriptionBox() {
        Label descriptionLabel = new Label((LanguageManager.getString("booking.form.description")));
        descriptionLabel.getStyleClass().add("form-field-label");

        descriptionField.setPrefRowCount(3);
        VBox descriptionBox = new VBox(descriptionLabel, descriptionField);
        descriptionBox.getStyleClass().add("form-field-container");
        return descriptionBox;
    }

    private ComboBox<Mechanic> createMechanicComboBox() {
        ObservableList<Mechanic> mechanics =
                FXCollections.observableArrayList(garageSystem.getAvailableMechanics());

        ComboBox<Mechanic> comboBox = new ComboBox<>(mechanics);

        comboBox.setPromptText(LanguageManager.getString("booking.chooseMechanic"));
        comboBox.setConverter(ComboBoxLabels.mechanic());

        return comboBox;
    }

    // =========================================================
    // ACTIONS
    // =========================================================
    private HBox createActionButtons() {
        Button cancelButton =
                new Button(LanguageManager.getString("booking.form.cancel"));
        cancelButton.getStyleClass().addAll("cancel-btn");

        Button createButton =
                new Button(LanguageManager.getString("booking.form.submit"));
        createButton.getStyleClass().add("confirm-btn");

        HBox btnBox = new HBox(
                cancelButton,
                createButton
        );
        btnBox.getStyleClass().add("btn-container");

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

        return btnBox;
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
}