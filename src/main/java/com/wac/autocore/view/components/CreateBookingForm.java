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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Optional;

public class CreateBookingForm {
    private final GarageSystem garageSystem;
    private final Stage popupStage;

    private final ObjectProperty<Vehicle> selectedVehicle =
            new SimpleObjectProperty<>();

    private final VBox vehicleResults = new VBox();
    private final VBox bookingInformation = new VBox();

    private final TextArea descriptionField = new TextArea();
    private ComboBox<Mechanic> mechanicComboBox;


    public CreateBookingForm(Stage popupStage, GarageSystem garageSystem) {
        this.popupStage = popupStage;
        this.garageSystem = garageSystem;
    }

    /**
     * User chooses customer and vehicle
     */
    public Parent show() {
        VBox layout = new VBox();

        CustomerSelection customerSelection = new CustomerSelection(
                garageSystem.getCustomers(),
                this::showVehiclesOfCustomer);
        customerSelection.getStyleClass().addAll("form-field-container");

        Label selectVehicleLabel = new Label(LanguageManager.getString("booking.form.selectVehicle"));
        selectVehicleLabel.getStyleClass().add("form-field-label");
        vehicleResults.getStyleClass().addAll("card-container", "compact");
        VBox selectVehicleBox = new VBox(selectVehicleLabel, vehicleResults);
        selectVehicleBox.getStyleClass().add("form-field-container");

        HBox actionButtons = createActionButtons();
        actionButtons.getStyleClass().add("btn-container");

        layout.getChildren().addAll(
                createTitle(),
                customerSelection,
                selectVehicleBox,
                getDescriptionBox(),
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
        Optional<Vehicle> optionalVehicle = garageSystem.getVehicle(vehicleId);

        if (!optionalVehicle.isPresent()) {
            AppDialog.showError(String.format(LanguageManager.getString("booking.error.noVehicle"),vehicleId),
                    String.format(LanguageManager.getString("booking.error.noVehicle"),vehicleId),
                    String.format(LanguageManager.getString("booking.error.noVehicle"),vehicleId));

            popupStage.close();
            return null;
        }
        Vehicle vehicle = optionalVehicle.get();

        Customer customer = vehicle.getCustomer();

        if (customer == null) {
            AppDialog.showError(LanguageManager.getString("booking.error.noCustomer"),
                    LanguageManager.getString("booking.error.noCustomer"),
                    LanguageManager.getString("booking.error.noCustomer"));
            popupStage.close();
            return null;
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

    //Behåller då den kan användas för
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