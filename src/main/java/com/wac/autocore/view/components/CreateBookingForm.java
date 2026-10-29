package com.wac.autocore.view.components;

import com.wac.autocore.model.*;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.StylingUtil;
import com.wac.autocore.util.ComboBoxLabels;
import com.wac.autocore.util.LanguageManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CreateBookingForm {
    private final GarageSystem garageSystem;
    private final Stage popupStage;

    private final ObjectProperty<Vehicle> selectedVehicle = new SimpleObjectProperty<>();
    private ObservableList<ServiceItem> selectedItems = FXCollections.observableArrayList();


    private final VBox vehicleResults = new VBox();

    private final TextArea descriptionField = new TextArea();
    private final ComboBox<WorkOrderType> typeComboBox = new ComboBox<>();
    private ComboBox<Mechanic> mechanicComboBox;


    public CreateBookingForm(Stage popupStage, GarageSystem garageSystem) {
        this.popupStage = popupStage;
        this.garageSystem = garageSystem;
            }

    /**
     * User chooses customer and vehicle and services
     */
    public Parent show(){
        return show(-1, new ArrayList<>());
    }

    /**
     * Vehicle is already chosen, no services chosen
     */
    public Parent show(int vehicleId) {
        return show(vehicleId, new ArrayList<>());
    }

    /**
     * Rebooking with vehicle and selected services.
     * @param vehicleId
     * @param preselectedServiceIds
     * @return
     */
    public Parent show(int vehicleId, List<Integer> preselectedServiceIds){
        VBox layout = new VBox();

        VBox vehicleContainer;
        //If vehicleId is greater than 0, get the vehicle card for selected vehicle (Shows error if no vehicle with selected Id exists)
        if(vehicleId > 0){
            VehicleCard vehicleCard = getVehicleCard(vehicleId);
            if(vehicleCard == null){
                popupStage.close();
                return null;
            }
            vehicleContainer = new VBox(vehicleCard);

        } else {
            //Else search for customer and vehicle
            Label customerLabel = new Label(LanguageManager.getString("customer.select.choose") + " (*)");
            customerLabel.getStyleClass().add("form-field-label");
            CustomerSelection customerSelection = new CustomerSelection(
                    garageSystem,
                    this::showVehiclesOfCustomer);
            customerSelection.getStyleClass().addAll("form-field-container");
            customerSelection.getChildren().addFirst(customerLabel);

            Label selectVehicleLabel = new Label(LanguageManager.getString("booking.form.selectVehicle") + " (*)");
            selectVehicleLabel.getStyleClass().add("form-field-label");
            vehicleResults.getStyleClass().addAll("card-container", "compact");
            VBox vehicleSelection = new VBox(selectVehicleLabel, vehicleResults);
            vehicleSelection.getStyleClass().add("form-field-container");

            vehicleContainer = new VBox(customerSelection, vehicleSelection);
        }

        VBox mechanicBox = getMechanicBox();

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        //Show service items
        selectedItems.addAll(garageSystem.getServiceItemsByIds(preselectedServiceIds));
        List<Integer> missingId = garageSystem.getMissingIds(preselectedServiceIds, selectedItems);

        //If some services has been removed, show this error message
        if(!missingId.isEmpty()){
            errorLabel.setText(String.format(
                    LanguageManager.getString("booking.form.error.missingServiceItems"),
                    missingId.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(", "))
            ));
        }

        Label selectServicesLabel = new Label(LanguageManager.getString("booking.form.selectServices"));
        selectServicesLabel.getStyleClass().add("form-field-label");
        ServiceItemSelection serviceItemSelection = new ServiceItemSelection(
                selectedItems,
                garageSystem.getServiceItems(),
                garageSystem.getServicePacks(),
                error -> errorLabel.setText(errorLabel.getText() + error));
        serviceItemSelection.getChildren().addFirst(selectServicesLabel);

        HBox actionButtons = createActionButtons();
        actionButtons.getStyleClass().add("btn-container");

        layout.getChildren().addAll(
                createTitle(),
                vehicleContainer,
                getDescriptionBox(),
                getTypeBox(),
                mechanicBox,
                serviceItemSelection,
                errorLabel,
                actionButtons
        );

        layout.getStyleClass().add("form-container");

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        return scrollPane;
    }

    private VehicleCard getVehicleCard(int vehicleId) {
        Optional<Vehicle> optionalVehicle = garageSystem.getVehicle(vehicleId);

        if (!optionalVehicle.isPresent()) {
            AppDialog.showError(String.format(LanguageManager.getString("booking.error.noVehicle"), vehicleId),
                    String.format(LanguageManager.getString("booking.error.noVehicle"), vehicleId),
                    String.format(LanguageManager.getString("booking.error.noVehicle"), vehicleId));
            return null;
        }
        Vehicle vehicle = optionalVehicle.get();

        Customer customer = vehicle.getCustomer();

        if (customer == null) {
            AppDialog.showError(LanguageManager.getString("booking.error.noCustomer"),
                    LanguageManager.getString("booking.error.noCustomer"),
                    LanguageManager.getString("booking.error.noCustomer"));
            return null;
        }

        VehicleCard vehicleCard = new VehicleCard(vehicle, customer);
        vehicleCard.getStyleClass().add("compact");
        selectedVehicle.set(vehicle);
        return vehicleCard;
    }

    private Node createTitle() {
        Label title = new Label(LanguageManager.getString("booking.form.title"));
        title.getStyleClass().add("form-title");
        Label explanation = new Label(LanguageManager.getString("booking.form.explanation"));

        return new VBox(title, explanation);
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
            selectedVehicle.set(vehicle);
            StylingUtil.setSelected(vehicleCard, "card");
        });

        vehicleCard.getStyleClass().add("clickable");

        return vehicleCard;
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

    private ComboBox<Mechanic> createMechanicComboBox() {
        ObservableList<Mechanic> mechanics =
                FXCollections.observableArrayList(garageSystem.getMechanics());

        ComboBox<Mechanic> comboBox = new ComboBox<>(mechanics);

        comboBox.setPromptText(LanguageManager.getString("booking.chooseMechanic"));
        comboBox.setConverter(ComboBoxLabels.mechanic());

        return comboBox;
    }

    private VBox getDescriptionBox() {
        Label descriptionLabel = new Label((LanguageManager.getString("booking.form.description")));
        descriptionLabel.getStyleClass().add("form-field-label");

        descriptionField.setPrefRowCount(3);
        VBox descriptionBox = new VBox(descriptionLabel, descriptionField);
        descriptionBox.getStyleClass().add("form-field-container");
        return descriptionBox;
    }

    private VBox getTypeBox() {
        Label typeLabel = new Label(LanguageManager.getString("workorder.type.label"));
        typeLabel.getStyleClass().add("form-field-label");

        typeComboBox.getItems().setAll(WorkOrderType.values());
        typeComboBox.setPromptText(LanguageManager.getString("workorder.type.prompt"));
        typeComboBox.setConverter(ComboBoxLabels.workOrderType());

        VBox typeBox = new VBox(typeLabel, typeComboBox);
        typeBox.getStyleClass().add("form-field-container");
        return typeBox;
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
                        .or(typeComboBox.valueProperty().isNull())
        );

        createButton.setOnAction(
                e ->
                        //TODO validate to garageSystem
                        //TODO garageSystem.createBookingAndWorkOrder som gör en unit of work
                        createBooking(
                            booking -> createWorkOrderDraft(booking)
                        )
        );

        cancelButton.setOnAction(
                e -> popupStage.close()
        );

        return btnBox;
    }

    private void createWorkOrderDraft(Booking booking) {
        Mechanic selectedMechanic = mechanicComboBox.getValue();

        int mechanicId = selectedMechanic == null
                ? 0
                : selectedMechanic.getId();

        int[] selectedServiceItemIds = selectedItems.stream()
                .mapToInt(ServiceItem::getId)
                .toArray();

        Node root = popupStage.getScene() != null
                ? popupStage.getScene().getRoot()
                : null;
        if (root != null) {
            root.setDisable(true);
        }

        WorkOrderType type = typeComboBox.getValue();

        Task<WorkOrder> task = new Task<WorkOrder>() {
            @Override
            protected WorkOrder call() {
                return garageSystem.createWorkOrder(
                        booking.getId(),
                        mechanicId,
                        type,
                        selectedServiceItemIds
                );
            }
        };

        task.setOnFailed(e -> {
            if (root != null) {
                root.setDisable(false);
            }

            Throwable exception = task.getException();
            AppDialog.showError(
                    LanguageManager.getString("booking.form.workOrder.error.title"),
                    LanguageManager.getString("booking.form.workOrder.error.header"),
                    exception != null
                            ? exception.getMessage()
                            : LanguageManager.getString("booking.form.workOrder.error.message")
            );
        });

        task.setOnSucceeded(e -> {
            WorkOrder createdWorkOrder = task.getValue();
            AppDialog.showInformation(
                            LanguageManager.getString("workorder.form.dialog.workorderCreated.title"),
                            LanguageManager.getString("workorder.form.dialog.workorderCreated.header"),
                            String.format(LanguageManager.getString("workorder.form.dialog.workorderCreated.message"),
                                    createdWorkOrder.getId())
                    );
            popupStage.close();

        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void createBooking(Consumer<Booking> onCreated) {
        Vehicle vehicle = selectedVehicle.get();

        if (vehicle == null) {
            return;
        }

        int vehicleId = vehicle.getId();
        String description = descriptionField.getText();
        String language = LanguageManager.getCurrentLanguage();

        Node root = popupStage.getScene() != null
                ? popupStage.getScene().getRoot()
                : null;
        if (root != null) {
            root.setDisable(true);
        }

        Task<Booking> task = new Task<Booking>() {
            @Override
            protected Booking call() {
                return garageSystem.createBookingWithTranslation(
                        vehicleId,
                        LocalDate.now(),
                        description,
                        language
                );
            }
        };

        task.setOnSucceeded(e -> {
            Booking savedBooking = task.getValue();
            onCreated.accept(savedBooking);
        });
        task.setOnFailed(e -> {
            Throwable exception = task.getException();

            AppDialog.showError(
                    LanguageManager.getString("booking.form.error.title"),
                    LanguageManager.getString("booking.form.error.header"),
                    exception != null ? exception.getMessage() : ""
            );

            if (root != null) {
                root.setDisable(false);
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }


}