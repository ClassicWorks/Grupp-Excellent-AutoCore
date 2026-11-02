package com.wac.autocore.view.components;

import com.wac.autocore.controller.*;
import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.*;
import com.wac.autocore.util.ComboBoxLabels;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.util.StylingUtil;
import com.wac.autocore.view.UiResultHandler;
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
    private final BookingController bookingcontroller;
    private final CatalogController catalogcontroller;
    private final VehicleController vehiclecontroller;
    private final MechanicController mechaniccontroller;
    private final WorkOrderController workOrderController;
    private final UiResultHandler uiResultHandler = new UiResultHandler();
    private final Stage popupStage;

    private final ObjectProperty<Vehicle> selectedVehicle = new SimpleObjectProperty<>();
    private final ObservableList<ServiceItem> selectedItems = FXCollections.observableArrayList();

    private final VBox vehicleResults = new VBox();
    private final TextArea descriptionField = new TextArea();
    private final ComboBox<WorkOrderType> typeComboBox = new ComboBox<>();
    private ComboBox<Mechanic> mechanicComboBox;


    public CreateBookingForm(Stage popupStage) {
        this.popupStage = popupStage;
        bookingcontroller = ViewManager.getInstance().getControllers().bookings();
        catalogcontroller = ViewManager.getInstance().getControllers().catalog();
        vehiclecontroller = ViewManager.getInstance().getControllers().vehicles();
        mechaniccontroller = ViewManager.getInstance().getControllers().mechanics();
        workOrderController = ViewManager.getInstance().getControllers().workOrders();
    }

    /**
     * User chooses customer and vehicle and services
     * @return View of form
     */
    public Parent show(){
        return show(-1, new ArrayList<>());
    }

    /**
     * Vehicle is already chosen, no services chosen
     * @param vehicleId Id of vehicle
     * @return View of form
     */
    public Parent show(int vehicleId) {
        return show(vehicleId, new ArrayList<>());
    }

    /**
     * Rebooking with vehicle and selected services.
     * @param vehicleId Id of vehicle
     * @param preselectedServiceIds Selected services
     * @return View of form
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
            CustomerSelection customerSelection = new CustomerSelection(this::showVehiclesOfCustomer);
            VBox customerBox = new FormFieldContainer(
                    LanguageManager.getString("customer.select.choose"),
                    customerSelection,
                    true
            );

            vehicleResults.getStyleClass().addAll("card-container", "compact");
            VBox vehicleSelection = new FormFieldContainer(
                    LanguageManager.getString("booking.form.selectVehicle"),
                    vehicleResults,
                    true
            );

            vehicleContainer = new VBox(customerBox, vehicleSelection);
        }

        VBox mechanicBox = getMechanicBox();

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        //Show service items
        VBox serviceItemSection = new VBox();
        Result<List<ServiceItem>> preselectedItemsResult = uiResultHandler.handle(catalogcontroller.getServiceItemsByIds(preselectedServiceIds));
        Result<List<ServiceItem>> serviceItemsResult = uiResultHandler.handle(catalogcontroller.getServiceItems());
        Result<List<ServicePack>> servicePacksResult = uiResultHandler.handle(catalogcontroller.getServicePacks());

        if(preselectedItemsResult.isSuccess() && serviceItemsResult.isSuccess() &&  servicePacksResult.isSuccess()) {
            selectedItems.addAll(preselectedItemsResult.value());
            Result<List<Integer>> missingIdResult = uiResultHandler.handle(catalogcontroller.getMissingIds(preselectedServiceIds, selectedItems));
            if(missingIdResult.isSuccess()) {
                List<Integer> missingId = missingIdResult.value();

                //If some services has been removed, show this error message
                if (!missingId.isEmpty()) {
                    errorLabel.setText(String.format(
                            LanguageManager.getString("booking.form.error.missingServiceItems"),
                            missingId.stream()
                                    .map(String::valueOf)
                                    .collect(Collectors.joining(", "))
                    ));
                }
            }
            Label selectServicesLabel = new Label(LanguageManager.getString("booking.form.selectServices"));
            selectServicesLabel.getStyleClass().add("form-field-label");

            ServiceItemSelection serviceItemSelection = new ServiceItemSelection(
                    selectedItems,
                    serviceItemsResult.value(),
                    servicePacksResult.value(),
                    error -> errorLabel.setText(errorLabel.getText() + error));
            serviceItemSelection.getChildren().addFirst(selectServicesLabel);
            serviceItemSection.getChildren().addAll(serviceItemSelection);
        }else {
            Label errorLoadingLabel = new Label(LanguageManager.getString("booking.form.errorLoading.serviceItems"));
            errorLoadingLabel.getStyleClass().add("error-label");
            serviceItemSection.getChildren().addAll(errorLoadingLabel);
        }

        HBox actionButtons = createActionButtons();
        actionButtons.getStyleClass().add("btn-container");

        layout.getChildren().addAll(
                createTitle(),
                vehicleContainer,
                getDescriptionBox(),
                getTypeBox(),
                mechanicBox,
                serviceItemSection,
                errorLabel,
                actionButtons
        );

        layout.getStyleClass().add("form-container");

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        return scrollPane;
    }

    private VehicleCard getVehicleCard(int vehicleId) {
        Result<Optional<Vehicle>> optionalResult = uiResultHandler.handle(vehiclecontroller.getVehicle(vehicleId));
        if(!optionalResult.isSuccess()) {
            return null;
        }
        Optional<Vehicle> optionalVehicle = optionalResult.value();

        if (optionalVehicle.isEmpty()) {
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
        VBox headerBox = new VBox(title);

        Label explanation = new Label(LanguageManager.getString("general.form.explanation"));
        explanation.setWrapText(true);
        explanation.setMinWidth(0);
        explanation.getStyleClass().add("form-explanation");
        headerBox.getChildren().add(explanation);

        return headerBox;
    }

    // =========================================================
    // VEHICLE SELECTION
    // =========================================================
    private void showVehiclesOfCustomer(Customer customer) {
        vehicleResults.getChildren().clear();
        selectedVehicle.set(null);
        Result<List<Vehicle>> vehiclesResult = uiResultHandler.handle(vehiclecontroller.getCustomersVehicle(customer.getId()));
        if(vehiclesResult.isSuccess()) {

            vehiclesResult.value()
                    .forEach(vehicle ->
                            vehicleResults
                                    .getChildren()
                                    .add(createSelectableVehicleCard(vehicle))
                    );
        }else {
            Label errorLabel = new Label(LanguageManager.getString("booking.form.errorLoading.vehiclesOfCustomer"));
            errorLabel.getStyleClass().add("error-label");
            vehicleResults.getChildren().addAll(errorLabel);
        }
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
        mechanicComboBox = createMechanicComboBox();

        return new FormFieldContainer(
                LanguageManager.getString("booking.form.mechanicOptional"),
                mechanicComboBox,
                false
        );
    }

    private ComboBox<Mechanic> createMechanicComboBox() {
        Result<List<Mechanic>> mechanicsResult = uiResultHandler.handle(mechaniccontroller.getMechanics());
        if(!mechanicsResult.isSuccess()) {
            ComboBox<Mechanic> errorBox = new ComboBox<>();
            errorBox.setDisable(true);
            errorBox.setPromptText(LanguageManager.getString("booking.form.errorLoading.mechanics"));
            return errorBox;
        }
        ObservableList<Mechanic> mechanics =
                FXCollections.observableArrayList(mechanicsResult.value());

        ComboBox<Mechanic> comboBox = new ComboBox<>(mechanics);

        comboBox.setPromptText(LanguageManager.getString("booking.chooseMechanic"));
        comboBox.setConverter(ComboBoxLabels.mechanic());

        return comboBox;
    }

    private VBox getDescriptionBox() {
        descriptionField.setPrefRowCount(3);
        return new FormFieldContainer(
                LanguageManager.getString("booking.form.description"),
                descriptionField,
                true
        );
    }

    private VBox getTypeBox() {
        typeComboBox.getItems().setAll(WorkOrderType.values());
        typeComboBox.setPromptText(LanguageManager.getString("workorder.type.prompt"));
        typeComboBox.setConverter(ComboBoxLabels.workOrderType());

        return new FormFieldContainer(
                LanguageManager.getString("workorder.type.label"),
                typeComboBox,
                true
        );
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
                        //TODO .createBookingAndWorkOrder som gör en unit of work
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

        Task<Result<WorkOrder>> task = new Task<>() {
            @Override
            protected Result<WorkOrder> call() {
                return workOrderController.create(
                            booking.getId(),
                            mechanicId,
                            type,
                            selectedServiceItemIds);
            }
        };

        task.setOnFailed(e -> {
            Throwable exception = task.getException();
            AppDialog.showError(
                    LanguageManager.getString("booking.form.workOrder.error.title"),
                    LanguageManager.getString("booking.form.workOrder.error.header"),
                    exception != null
                            ? exception.getMessage()
                            : LanguageManager.getString("booking.form.workOrder.error.message")
            );
            if (root != null) {
                root.setDisable(false);
            }
        });

        task.setOnSucceeded(e -> {
            Result<WorkOrder> createdWorkOrderResult = task.getValue();
            if(!createdWorkOrderResult.isSuccess()){
                uiResultHandler.handle(createdWorkOrderResult);
            }else {
                AppDialog.showInformation(
                        LanguageManager.getString("workorder.form.dialog.workorderCreated.title"),
                        LanguageManager.getString("workorder.form.dialog.workorderCreated.header"),
                        String.format(LanguageManager.getString("workorder.form.dialog.workorderCreated.message"),
                                createdWorkOrderResult.value().getId())
                );
                popupStage.close();
            }
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

        Task<Result<Booking>> task = new Task<>() {
            @Override
            protected Result<Booking> call() {
                return bookingcontroller.createBooking(
                                vehicleId,
                                LocalDate.now(),
                                description,
                                language);
            }
        };

        task.setOnSucceeded(e -> {
            Result<Booking> result = task.getValue();

            if (!result.isSuccess()) {
                uiResultHandler.handle(result);
                if (root != null) {
                    root.setDisable(false);
                }
            } else {
                onCreated.accept(result.value());
            }
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
