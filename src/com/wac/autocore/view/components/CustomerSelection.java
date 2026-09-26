package com.wac.autocore.view.components;

import com.wac.autocore.model.Customer;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CustomerSelection extends VBox {
    private final List<Customer> customers;
    private final Consumer<Customer> onCustomerSelected;
    private final VBox customerResults = new VBox();
    private final Label selectedCustomer = new Label();

    public CustomerSelection(List<Customer> customers, Consumer<Customer> onCustomerSelected) {
        this.customers = customers;
        this.onCustomerSelected = onCustomerSelected;

        getChildren().add(createCustomerSelection());
    }

    private Node createCustomerSelection() {
        Label label = new Label("Choose customer");
        TextField searchField = new TextField();

        selectedCustomer.getStyleClass().add("selected-customer");

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        updateCustomerResults(newValue)
        );

        return new VBox(label, searchField, customerResults, selectedCustomer);
    }


    private void updateCustomerResults(String query) {
        customerResults.getChildren().clear();

        List<Customer> customerMatch = customers.stream()
                .filter(customer ->
                        customer.getName()
                                .toLowerCase()
                                .contains(query.toLowerCase())
                )
                .collect(Collectors.toList());

        //Add different styling to even and uneven customers
        for(int i = 0; i < customerMatch.size(); i++){
            Node rowOfCustomer = createCustomerResult(customerMatch.get(i));
            String rowStyle = i % 2 == 0
                    ? "row-even"
                    : "row-odd";

            rowOfCustomer.getStyleClass().add(rowStyle);
            customerResults.getChildren().add(rowOfCustomer);
        }
    }


    private Node createCustomerResult(Customer customer) {
        Label customerInfo = new Label(
                String.format(
                        "ID: %d - %s",
                        customer.getId(),
                        customer.getName()
                )
        );
        HBox row = new HBox(customerInfo);
        row.setMaxWidth(Double.MAX_VALUE);
        row.getStyleClass().add("customer-row");

        row.setOnMouseClicked(e -> {
            selectedCustomer.setText(String.format("Selected customer: ID: %d - %s",
                        customer.getId(),
                        customer.getName())
            );
            onCustomerSelected.accept(customer);
        });

        return row;
    }
}
