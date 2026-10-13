package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.*;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.util.ValueLabels;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class WorkOrderDetails extends BorderPane {

    private final GarageSystem garageSystem = new GarageSystem();
    private final WorkOrder workOrder;
    private final Runnable onChanged;

    public WorkOrderDetails(WorkOrder workOrder, Runnable onChanged) {
        this.workOrder = workOrder;
        this.onChanged = onChanged;

        this.setTop(createHeader());
        this.setCenter(createInfoSection());
        this.setBottom(createActionButtons());
    }

    private HBox createHeader() {
        Label titleLabel = new Label(String.format(
                LanguageManager.getString("workorder.card.title"), workOrder.getId()));
        titleLabel.getStyleClass().add("form-title");

        Label statusLabel = new Label(ValueLabels.workOrderStatus(workOrder.getStatus()));
        statusLabel.getStyleClass().addAll(
                "status-label",
                workOrder.getStatus().name().toLowerCase().replace('_', '-')
        );

        return new HBox(10, titleLabel, statusLabel);
    }

    private VBox createInfoSection() {
        Booking booking = workOrder.getBooking();
        Vehicle vehicle = booking != null ? booking.getVehicle() : null;
        Customer customer = vehicle != null ? vehicle.getCustomer() : null;
        Mechanic mechanic = workOrder.getMechanic();

        Label vehicleLabel = new Label(vehicle != null
                ? String.format("%s | %s %s, %d", vehicle.getRegistrationNumber(),
                vehicle.getBrand(), vehicle.getModel(), vehicle.getYear())
                : LanguageManager.getString("workorders.error.vehicleNotFound"));

        Label customerLabel = new Label(customer != null
                ? customer.getName()
                : LanguageManager.getString("customer.none.connected"));

        Label mechanicLabel = new Label(mechanic != null
                ? mechanic.getName()
                : LanguageManager.getString("mechanic.none.assigned"));


        VBox info = new VBox(
                createFormFieldContainer("workorder.details.vehicle", vehicleLabel),
                createFormFieldContainer("workorder.details.customer", customerLabel),
                createFormFieldContainer("workorder.details.mechanic", mechanicLabel)
        );

        if (booking != null) {
            Label dateLabel = new Label(booking.getDate().toString());

            Label descriptionLabel = new Label(
                    booking.getDescriptionFor(LanguageManager.getCurrentLanguage()));
            descriptionLabel.setWrapText(true);

            info.getChildren().addAll(
                    createFormFieldContainer("booking.date", dateLabel),
                    createFormFieldContainer("workorder.details.description", descriptionLabel)
            );
        }

        info.getChildren().addAll(createServiceSection());

        info.getStyleClass().add("details-container");
        return info;
    }

    private VBox createFormFieldContainer(String key, Label vehicleLabel) {
        VBox vehicleInfo = new VBox(createFieldLabel(key), vehicleLabel);
        vehicleInfo.getStyleClass().add("form-field-container");
        return vehicleInfo;
    }

    private Label createFieldLabel(String key) {
        Label label = new Label(LanguageManager.getString(key));
        label.getStyleClass().add("form-field-label");
        return label;
    }

    private VBox createServiceSection() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(5);

        int row = 0;
        int totalMinutes = 0;

        for (WorkOrderItem item : workOrder.getItems()) {
            ServiceItem serviceItem = item.getServiceItem();

            grid.add(new Label(ValueLabels.serviceName(serviceItem.getName())), 0, row);
            grid.add(new Label(String.format(
                    LanguageManager.getString("service.minutes"), serviceItem.getEstimatedMinutes())), 1, row);
            grid.add(new Label(formatAmount(item.getPriceAtOrder())), 2, row);

            totalMinutes += serviceItem.getEstimatedMinutes();
            row++;
        }

        Label totalLabel = new Label(
                LanguageManager.getString("workorder.details.total"));
        Label totalMinutesLabel = new Label(String.format(
                LanguageManager.getString("service.minutes"),
                totalMinutes));
        Label totalPriceLabel = new Label(formatAmount(workOrder.getTotalPrice()));

        for (Label label : Arrays.asList(
                totalLabel, totalMinutesLabel, totalPriceLabel)) {
            label.getStyleClass().addAll("row-final");
        }

        grid.add(totalLabel, 0, row);
        grid.add(totalMinutesLabel, 1, row);
        grid.add(totalPriceLabel, 2, row);


        return new VBox(createFieldLabel("workorder.serviceItems"), grid);
    }

    private String formatAmount(double amount) {
        return String.format(LanguageManager.getString("invoice.details.amountFormat"), amount);
    }

    private HBox createActionButtons() {
        HBox buttons = new HBox();
        buttons.getStyleClass().add("btn-container");

        WorkOrderStatus status = workOrder.getStatus();

        if (status.canEditServices()) {
            buttons.getChildren().add(createButton("workorder.editServices", "confirm-btn",
                    () -> ViewManager.getInstance().showEditWorkOrderItemsPopUp(workOrder.getId(), onChanged)));
        }

        if (status == WorkOrderStatus.DRAFT) {
            buttons.getChildren().add(createButton("workorder.confirm", "confirm-btn", () -> {
                try {
                    garageSystem.confirmWorkOrder(workOrder.getId());
                } catch (IllegalArgumentException e) {
                    AppDialog.showError("Could not continue",
                            "Something went wrong",
                            e.getMessage());
                }
                onChanged.run();
            }));
        }

        if (status == WorkOrderStatus.CONFIRMED) {
            buttons.getChildren().add(createButton("workorder.start", "confirm-btn", () -> {
                try {
                    garageSystem.startWorkOrder(workOrder.getId());
                } catch (IllegalArgumentException e) {
                    AppDialog.showError("Could not continue",
                            "Something went wrong",
                            e.getMessage());
                }
                onChanged.run();
            }));
        }

        if (status == WorkOrderStatus.IN_PROGRESS) {
            buttons.getChildren().add(createButton("workorder.complete", "confirm-btn", () -> {
                try {
                    garageSystem.completeWorkOrder(workOrder.getId());
                } catch (IllegalArgumentException e) {
                    AppDialog.showError(LanguageManager.getString("workorder.dialog.error.title"),
                            LanguageManager.getString("workorder.dialog.error.header"),
                            e.getMessage());
                }
                onChanged.run();
            }));
        }

        if (status == WorkOrderStatus.COMPLETED) {
            buttons.getChildren().add(createButton("workorder.rebook", "confirm-btn", this::rebook));
        }

        if (status.canBeCancelled()) {
            buttons.getChildren().add(createButton("workorder.cancel", "destroy-btn",
                    this::cancelWithConfirmation));
        }

        if (buttons.getChildren().isEmpty()) {
            buttons.getChildren().add(new Label(LanguageManager.getString("workorder.noAction")));
        }

        return buttons;
    }

    private Button createButton(String textKey, String styleClass, Runnable action) {
        Button button = new Button(LanguageManager.getString(textKey));
        button.getStyleClass().add(styleClass);
        button.setOnAction(e -> action.run());
        return button;
    }

    private void cancelWithConfirmation() {
        boolean confirmed = AppDialog.showConfirm(
                LanguageManager.getString("workorder.cancel.dialog.title"),
                String.format(LanguageManager.getString("workorder.cancel.dialog.header"), workOrder.getId()),
                LanguageManager.getString("workorder.cancel.dialog.message"));

        if (confirmed) {
            garageSystem.cancelWorkOrder(workOrder.getId());
            onChanged.run();
        }
    }

    private void rebook() {
        //Möjlighet för dto? Då kan felhantering ske i GarageSystem
        Booking booking = workOrder.getBooking();
        if (booking == null) {
            AppDialog.showError(
                    LanguageManager.getString("workorder.rebook.error.bookingNotFound.title"),
                    LanguageManager.getString("workorder.rebook.error.bookingNotFound.header"),
                    String.format(
                            LanguageManager.getString("workorder.rebook.error.bookingNotFound.message"),
                            workOrder.getId()
                    )
            );
            return;
        }
        if(booking.getVehicle() == null){
            AppDialog.showError(
                    LanguageManager.getString("workorder.rebook.error.vehicleNotFound.title"),
                    LanguageManager.getString("workorder.rebook.error.vehicleNotFound.header"),
                    String.format(
                            LanguageManager.getString("workorder.rebook.error.vehicleNotFound.message"),
                            workOrder.getId()
                    )
            );
            return;
        }

        AtomicInteger numOfMissingItems = new AtomicInteger();

        List<Integer> serviceIds = workOrder.getItems().stream()
                .peek(item -> {
                    if (item == null) {
                        numOfMissingItems.getAndIncrement();
                    }
                }).filter(Objects::nonNull)
                .map(item -> item.getServiceItem().getId())
                .collect(Collectors.toList());

        //If some services has been removed, show this error message
        if(numOfMissingItems.get() > 0){
            AppDialog.showWarning(
                    LanguageManager.getString("workorder.rebook.warning.itemsNotFound.title"),
                    LanguageManager.getString("workorder.rebook.warning.itemsNotFound.header"),
                    String.format(
                            LanguageManager.getString("workorder.rebook.warning.itemsNotFound.message"),
                            numOfMissingItems.get()
                    )
            );
        }

        ViewManager.getInstance().showCreateBooking(booking.getVehicle().getId(), serviceIds);
    }
}
