package com.wac.autocore.view.components;

import com.wac.autocore.exceptions.BusinessRuleException;
import com.wac.autocore.exceptions.ValidationException;
import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.*;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.ComboBoxLabels;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.util.ValueLabels;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
        titleLabel.getStyleClass().add("details-title");

        Label statusLabel = new Label(ValueLabels.workOrderStatus(workOrder.getStatus()));
        statusLabel.getStyleClass().addAll(
                "status-label",
                workOrder.getStatus().name().toLowerCase().replace('_', '-')
        );

        //TODO styling through CSS, maybe borderPane
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

        CustomerHyperLink customerLink = new CustomerHyperLink(customer);


        VBox info = new VBox(
                createFormFieldContainer("workorder.details.vehicle", vehicleLabel),
                createFormFieldContainer("workorder.details.customer", customerLink),
                createFormFieldContainer("workorder.type.label", createTypeNode()),
                createFormFieldContainer("workorder.details.mechanic", createMechanicNode())
        );

        if (booking != null) {
            Label dateLabel = new Label(booking.getDate().toString());

            Label descriptionLabel = new Label(
                    booking.getDescriptionFor(LanguageManager.getCurrentLanguage()));
            descriptionLabel.setWrapText(true);

            info.getChildren().addAll(
                    createFormFieldContainer("workorder.details.date", dateLabel),
                    createFormFieldContainer("workorder.details.description", descriptionLabel)
            );
        }else {
            Label noBookingError = new Label(LanguageManager.getString("workorder.details.noBooking"));
            noBookingError.getStyleClass().add("error-label");
            info.getChildren().add(noBookingError);
        }

        info.getChildren().addAll(createServiceSection());

        info.getStyleClass().add("details-container");
        return info;
    }

    private VBox createFormFieldContainer(String key, Node content) {
        VBox vehicleInfo = new VBox(createFieldLabel(key), content);
        vehicleInfo.getStyleClass().add("form-field-container");
        return vehicleInfo;
    }

    private Label createFieldLabel(String key) {
        Label label = new Label(LanguageManager.getString(key));
        label.getStyleClass().add("form-field-label");
        return label;
    }

    private Node createTypeNode() {
        if (!workOrder.getStatus().canEdit()) {
            return new Label(ValueLabels.workOrderType(workOrder.getType()));
        }

        ComboBox<WorkOrderType> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().setAll(WorkOrderType.values());
        typeComboBox.setConverter(ComboBoxLabels.workOrderType());
        typeComboBox.setPromptText(LanguageManager.getString("workorder.type.prompt"));
        typeComboBox.setValue(workOrder.getType());

        typeComboBox.setOnAction(e -> {
            WorkOrderType selected = typeComboBox.getValue();
            if (selected == null || selected == workOrder.getType()) {
                return;
            }
            withErrorHandling(() -> garageSystem.updateWorkOrderType(workOrder.getId(), selected)).run();
        });

        return typeComboBox;
    }

    //SKa den verkligen sparas så fort man ändrar?
    private Node createMechanicNode(){
        if (!workOrder.getStatus().canEdit()) {
            return new MechanicHyperLink(workOrder.getMechanic());
        }

        ComboBox<Mechanic> mechanicComboBox = new ComboBox<>();
        mechanicComboBox.getItems().setAll(garageSystem.getMechanics());
        mechanicComboBox.setConverter(ComboBoxLabels.mechanic());
        mechanicComboBox.setPromptText(LanguageManager.getString("workorder.details.mechanic.prompt"));
        mechanicComboBox.setValue(workOrder.getMechanic());

        mechanicComboBox.setOnAction(e -> {
            Mechanic selected = mechanicComboBox.getValue();
            if (selected == workOrder.getMechanic()) {
                return;
            }
            withErrorHandling(() -> garageSystem.updateWorkOrderMechanic(workOrder.getId(), selected.getId())).run();
        });

        return mechanicComboBox;
    }

    private VBox createServiceSection() {
        //TODO styling with CSS
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

        if (status.canEdit()) {
            buttons.getChildren().add(createButton("workorder.editServices", "confirm-btn",
                    () -> ViewManager.getInstance().showEditWorkOrderItemsPopUp(workOrder.getId(), onChanged)));
        }

        if (status == WorkOrderStatus.DRAFT) {
            buttons.getChildren().add(createButton("workorder.confirm", "confirm-btn",
                    withErrorHandling(() -> garageSystem.confirmWorkOrder(workOrder.getId()))));
        }

        if (status == WorkOrderStatus.CONFIRMED) {
            buttons.getChildren().add(createButton("workorder.start", "confirm-btn",
                    withErrorHandling(() -> garageSystem.startWorkOrder(workOrder.getId()))));
        }

        if (status == WorkOrderStatus.IN_PROGRESS) {
            buttons.getChildren().add(createButton("workorder.complete", "confirm-btn",
                    withErrorHandling(() -> garageSystem.completeWorkOrder(workOrder.getId()))));
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

    private Runnable withErrorHandling(Runnable action) {
        return () -> {
            try {
                action.run();
            } catch (BusinessRuleException | ValidationException e) {
                AppDialog.showError(
                        LanguageManager.getString("workorder.dialog.error.title"),
                        LanguageManager.getString("workorder.dialog.error.header"),
                        e.getMessage());
            }
            onChanged.run();
        };
    }

    private void cancelWithConfirmation() {
        boolean confirmed = AppDialog.showConfirm(
                LanguageManager.getString("workorder.cancel.dialog.title"),
                String.format(LanguageManager.getString("workorder.cancel.dialog.header"), workOrder.getId()),
                LanguageManager.getString("workorder.cancel.dialog.message"));

        if (confirmed) {
            withErrorHandling(() -> garageSystem.cancelWorkOrder(workOrder.getId())).run();
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