package com.wac.autocore.view.components;

import com.wac.autocore.model.Customer;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class CustomerSelection extends VBox {
    private final GarageSystem garageSystem;
    private final Consumer<Customer> onCustomerSelected;
    private final VBox customerResults = new VBox();
    private final Label selectedCustomerLabel = new Label();

    public CustomerSelection(GarageSystem garageSystem, Consumer<Customer> onCustomerSelected) {
        this.garageSystem = garageSystem;
        this.onCustomerSelected = onCustomerSelected;

        this.getStyleClass().addAll("form-field-container");

        createCustomerSelection();
    }

    private void createCustomerSelection() {
        Label label = new Label(LanguageManager.getString("customer.select.choose"));
        label.getStyleClass().add("form-field-label");
        TextField searchField = new TextField("");
        searchField.setPromptText(LanguageManager.getString("customer.select.searchPrompt"));

        selectedCustomerLabel.setStyle(selectedCustomerLabel.getStyle() + " -fx-font-weight: 700;");

        searchField.setOnAction(event ->
                updateCustomerResults(searchField.getText())
        );

        ImageViewWithAltText searchIcon = new ImageViewWithAltText("imgs/magnifying-glass-solid.png", 15,15,LanguageManager.getString("image.magnifying-glass-solid"));
        Button searchBtn = new Button("", searchIcon);
        searchBtn.setOnAction(event ->
                updateCustomerResults(searchField.getText())
        );
        HBox searchBox = new HBox(searchField, searchBtn);
        searchBox.getStyleClass().add("form-field-container");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        this.getChildren().addAll(label, searchBox, customerResults, selectedCustomerLabel);
    }


    private void updateCustomerResults(String query) {
        customerResults.getChildren().clear();

        List<Customer> customerMatch = garageSystem.getCustomerMatching(query);

        //Add different styling to even and uneven customers
        for(int i = 0; i < customerMatch.size(); i++){
            HBox rowOfCustomer = createCustomerResult(customerMatch.get(i));
            String rowStyle = i % 2 == 0
                    ? "row-even"
                    : "row-odd";

            rowOfCustomer.getStyleClass().add(rowStyle);
            customerResults.getChildren().add(rowOfCustomer);
        }
    }


    private HBox createCustomerResult(Customer customer) {
        Label customerInfo = new Label(
                String.format(
                        "ID: %d - %s",
                        customer.getId(),
                        customer.getName()
                )
        );
        HBox row = new HBox(customerInfo);
        row.setMaxWidth(Double.MAX_VALUE);
        row.getStyleClass().addAll("clickable");

        row.setOnMouseClicked(e -> {
            selectedCustomerLabel.setText(String.format(LanguageManager.getString("customer.select.selected"),
                    customer.getId(),
                    customer.getName())
            );
            onCustomerSelected.accept(customer);
        });

        return row;
    }
}