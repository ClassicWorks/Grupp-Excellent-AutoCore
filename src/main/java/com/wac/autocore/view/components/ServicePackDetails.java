package com.wac.autocore.view.components;

import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.ServicePack;
import com.wac.autocore.util.ComboBoxLabels;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.util.ValueLabels;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ServicePackDetails extends BorderPane{

    //Title
    //Textfield for name
    //list of current ServiceItems
    //ComboBox other services w btn add
    //Buttons(save, delete)

    private ServicePack servicePack;
    private List<ServiceItem> allServiceItems;

    private Label errorLabel;
    private VBox currentServicesRows;
    private ComboBox<ServiceItem> serviceComboBox;

    public ServicePackDetails(ServicePack servicePack,
                              List<ServiceItem> allServiceItems,
                              Consumer<ServicePack> onSave,
                              Consumer<ServicePack> onDelete){
        this.servicePack = servicePack;
        this.allServiceItems = allServiceItems;

        errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        Label title = createTitle(servicePack.getId());

        Label nameLabel = new Label("Name of Service Pack");
        nameLabel.getStyleClass().add("form-field-label");
        TextField nameField = new TextField(servicePack.getName());
        VBox nameBox = new VBox(nameLabel, nameField);
        nameBox.getStyleClass().add("form-field-container");

        //Show current services in pack
        Label currentServicesLabel = new Label("Services in pack");
        currentServicesLabel.getStyleClass().add("form-field-label");

        currentServicesRows = new VBox();
        currentServicesRows.getStyleClass().add("row-container");

        VBox currentServicesContainer = new VBox(currentServicesLabel, currentServicesRows);
        VBox addServiceSection = createAddServiceSection(servicePack);

        refreshServiceItems();

        VBox detailsContainer = new VBox(
                nameBox,
                currentServicesContainer,
                addServiceSection
        );
        detailsContainer.getStyleClass().add("details-container");


        //Buttons
        Button saveBtn = createSaveBtn(onSave);
        HBox confirmingBtns = new HBox(saveBtn);
        confirmingBtns.getStyleClass().add("btn-container");

        Button deleteBtn = createDeleteBtn(onDelete);
        HBox destructiveBtns = new HBox(deleteBtn);
        destructiveBtns.getStyleClass().add("btn-container");

        BorderPane btnContainer = new BorderPane();
        btnContainer.setLeft(confirmingBtns);
        btnContainer.setRight(destructiveBtns);

        this.setTop(title);
        this.setCenter(detailsContainer);
        this.setBottom(btnContainer);
    }

    private void refreshServiceItems() {
        currentServicesRows.getChildren().clear();

        //Add row for every
        List<ServiceItem> serviceItems = servicePack.getServiceItems();
        boolean isLastItem = serviceItems.size() <= 1;
        for (ServiceItem item : serviceItems) {
            currentServicesRows.getChildren().add(createItemRow(item, isLastItem));
        }

        //Show total price and time for ServicePack
        double totalPrice = serviceItems.stream()
                .mapToDouble(ServiceItem::getPrice)
                .sum();
        int totalTime = serviceItems.stream()
                .mapToInt(ServiceItem::getEstimatedMinutes)
                .sum();

        Label priceAndTimeLabel = new Label(String.format("Total price: %.0f SEK | Total time: %d min",
                totalPrice,
                totalTime
        ));

        HBox finalRow = new HBox(priceAndTimeLabel);
        finalRow.getStyleClass().addAll("row", "row-final");
        finalRow.setAlignment(Pos.CENTER_RIGHT);
        currentServicesRows.getChildren().add(finalRow);

        List<ServiceItem> availableServices = allServiceItems.stream()
                .filter(serviceItem -> !servicePack.hasService(serviceItem.getId()))
                .collect(Collectors.toList());

        serviceComboBox.getItems().setAll(FXCollections.observableArrayList(availableServices));
        serviceComboBox.setPromptText("Choose service to add");
        serviceComboBox.setConverter(ComboBoxLabels.serviceItem());
    }

    private Node createItemRow(ServiceItem item, boolean isLastItem) {
        Label nameLabel = new Label(ValueLabels.serviceName(item.getName()));
        Label priceLabel = new Label(String.format("%.0f kr", item.getPrice()));

        Button removeBtn = new Button(LanguageManager.getString("workorder.edit.remove"));
        removeBtn.setDisable(isLastItem);
        removeBtn.getStyleClass().addAll("destroy-btn", "smaller");
        removeBtn.setOnAction(e -> {
            boolean success = servicePack.removeServiceItem(item);
                if(!success) {
                    errorLabel.setText(String.format("Could not remove service item with id %d", item.getId()));
                    return;
                }
            refreshServiceItems();
        });

        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        HBox row = new HBox(nameLabel, priceLabel, removeBtn);
        row.getStyleClass().add("row");
        row.setMaxWidth(Double.MAX_VALUE);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox createAddServiceSection(ServicePack servicePack) {
        serviceComboBox = new ComboBox<>();

        Button addBtn = new Button("Add service");
        addBtn.getStyleClass().add("confirm-btn");
        addBtn.disableProperty().bind(serviceComboBox.valueProperty().isNull());
        addBtn.setOnAction(e -> {
            ServiceItem selected = serviceComboBox.getValue();
            boolean success = servicePack.addServiceItem(selected);
            if (!success) {
                errorLabel.setText(String.format("Could not add service item with id %d", selected.getId()));
                return;
            }
            refreshServiceItems();
        });

        Label addLabel = new Label("Add more services to pack");
        addLabel.getStyleClass().add("form-field-label");

        HBox comboBoxContainer = new HBox(serviceComboBox, addBtn);
        comboBoxContainer.getStyleClass().add("row");
        VBox container = new VBox(addLabel, comboBoxContainer);
        container.getStyleClass().add("form-field-container");
        return container;
    }

    private Label createTitle(int id) {

        return new Label("dfghsdfh");
    }

    private Button createSaveBtn(Consumer<ServicePack> onSave) {
        return new Button("Save");
    }

    private Button createDeleteBtn(Consumer<ServicePack> onDelete) {

        return new Button("delete");
    }
}
