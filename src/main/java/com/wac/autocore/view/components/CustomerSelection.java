package com.wac.autocore.view.components;

import com.wac.autocore.model.Customer;
import com.wac.autocore.util.LanguageManager;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.hibernate.boot.jaxb.internal.stax.HbmEventReader;

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

        this.getStyleClass().addAll("form-field-container");

        createCustomerSelection();
    }

    private void createCustomerSelection() {
        Label label = new Label(LanguageManager.getString("customer.select.choose"));
        label.getStyleClass().add("form-field-label");
        TextField searchField = new TextField("");
        searchField.setPromptText(LanguageManager.getString("customer.select.search"));

        selectedCustomer.setStyle(selectedCustomer.getStyle() + " -fx-font-weight: 700;");

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        updateCustomerResults(newValue)
        );

        this.getChildren().addAll(label, searchField, customerResults, selectedCustomer);
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
            selectedCustomer.setText(String.format(LanguageManager.getString("customer.select.selected"),
                    customer.getId(),
                    customer.getName())
            );
            onCustomerSelected.accept(customer);
        });

        return row;
    }
}