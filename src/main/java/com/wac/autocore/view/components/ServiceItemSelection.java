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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ServiceItemSelection extends VBox{
    private List<ServiceItem> chosenItems;
    private List<ServiceItem> allServiceItems;
    private List<ServicePack> allServicePacks;
    private Consumer<String> onError;

    private VBox chosenServicesRows;
    private ComboBox<ServiceItem> serviceComboBox;

    //TODO make it able to handle serviceItems and WorkOrderItems -> seperate into two components: ItemRow and ServiceSelection and let parent build
    public ServiceItemSelection(List<ServiceItem> chosenItems,
                                List<ServiceItem> allServiceItems,
                                Consumer<String> onError) {
        this(chosenItems,
                allServiceItems,
                new ArrayList<>(),
                onError);
    }

    public ServiceItemSelection(List<ServiceItem> chosenItems,
                                List<ServiceItem> allServiceItems,
                                List<ServicePack> allServicePacks,
                                Consumer<String> onError) {
        this.chosenItems = chosenItems;
        this.allServiceItems = allServiceItems;
        this.allServicePacks = allServicePacks;
        this.onError = onError;

        // Show current chosen services

        chosenServicesRows = new VBox();
        chosenServicesRows.getStyleClass().add("row-container");

        VBox currentServicesContainer = new VBox( chosenServicesRows);
        VBox addServiceSection = createAddServiceSection();

        //Get and set Service items
        refreshServiceItems();

        this.getChildren().addAll(currentServicesContainer, addServiceSection);
        if (!allServicePacks.isEmpty()) {
            this.getChildren().add(createAddPackSection());
        }
        this.getStyleClass().addAll("form-field-container");
    }

    private void refreshServiceItems() {
        chosenServicesRows.getChildren().clear();

        //Add row for every chosen serviceItem
        boolean isLastItem = chosenItems.size() <= 1;
        if(chosenItems.isEmpty()){
            Label noItemsInListLabel = new Label(LanguageManager.getString("servicePack.form.emptyList"));
            chosenServicesRows.getChildren().add(noItemsInListLabel);
        }
        for (ServiceItem item : chosenItems) {
            chosenServicesRows.getChildren().add(createItemRow(item, isLastItem));
        }

        //TODO Make ServiceItem and ServicePack be part of Composite pattern?
        //Show total price and time
        double totalPrice = chosenItems.stream()
                .mapToDouble(ServiceItem::getPrice)
                .sum();
        int totalTime = chosenItems.stream()
                .mapToInt(ServiceItem::getEstimatedMinutes)
                .sum();

        Label priceAndTimeLabel = new Label(String.format(LanguageManager.getString("servicePack.totalPriceAndTime"),
                totalPrice,
                totalTime
        ));

        HBox finalRow = new HBox(priceAndTimeLabel);
        finalRow.getStyleClass().addAll("row", "row-final");
        finalRow.setAlignment(Pos.CENTER_RIGHT);
        chosenServicesRows.getChildren().add(finalRow);


        //Update combobox
        List<Integer> chosenIds = chosenItems.stream()
                .map(ServiceItem::getId)
                .collect(Collectors.toList());

        List<ServiceItem> availableServices = allServiceItems.stream()
                .filter(item -> !chosenIds.contains(item.getId()))
                .collect(Collectors.toList());

        serviceComboBox.getItems().setAll(FXCollections.observableArrayList(availableServices));
        serviceComboBox.setValue(null);
    }

    private boolean isChosen(ServiceItem serviceItem) {
        return chosenItems.stream()
                .anyMatch(chosen -> chosen.getId() == serviceItem.getId());
    }

    private Node createItemRow(ServiceItem item, boolean isLastItem) {
        Label nameLabel = new Label(ValueLabels.serviceName(item.getName()));
        Label priceLabel = new Label(String.format(LanguageManager.getString("service.priceAndTime"),
                item.getPrice(), item.getEstimatedMinutes())
        );

        Button removeBtn = new Button(LanguageManager.getString("workorder.edit.remove"));
        removeBtn.setDisable(isLastItem);
        removeBtn.getStyleClass().addAll("destroy-btn", "smaller");
        removeBtn.setOnAction(e -> {
            chosenItems.remove(item);

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

    private VBox createAddServiceSection() {
        serviceComboBox = new ComboBox<>();
        serviceComboBox.setPromptText(LanguageManager.getString("servicePack.addService.prompt"));
        serviceComboBox.setConverter(ComboBoxLabels.serviceItem());

        Button addBtn = new Button(LanguageManager.getString("servicePack.addService.button"));
        addBtn.getStyleClass().add("confirm-btn");

        addBtn.disableProperty().bind(serviceComboBox.valueProperty().isNull());

        addBtn.setOnAction(e -> {
            ServiceItem selected = serviceComboBox.getValue();
            chosenItems.add(selected);

            refreshServiceItems();
        });

        HBox comboBoxContainer = new HBox(serviceComboBox, addBtn);
        comboBoxContainer.getStyleClass().add("row");
        return new FormFieldContainer(
                LanguageManager.getString("servicePack.addService.label"), comboBoxContainer, false);
    }

    private VBox createAddPackSection() {
        ComboBox<ServicePack> packComboBox =
                new ComboBox<>(FXCollections.observableArrayList(allServicePacks));
        packComboBox.setPromptText(LanguageManager.getString("workorder.pack.prompt"));
        packComboBox.setConverter(ComboBoxLabels.servicePack());

        Button addPackBtn = new Button(LanguageManager.getString("workorder.pack.add"));
        addPackBtn.getStyleClass().add("confirm-btn");
        addPackBtn.disableProperty().bind(packComboBox.valueProperty().isNull());

        addPackBtn.setOnAction(e -> {
            ServicePack selected = packComboBox.getValue();
            for (ServiceItem item : selected.getServiceItems()) {
                if (!isChosen(item)) {
                    chosenItems.add(item);
                }
            }
            packComboBox.setValue(null);
            refreshServiceItems();
        });

        HBox comboBoxContainer = new HBox(packComboBox, addPackBtn);
        comboBoxContainer.getStyleClass().add("row");
        return new FormFieldContainer(
                LanguageManager.getString("workorder.pack.label"), comboBoxContainer, false);
    }
}
