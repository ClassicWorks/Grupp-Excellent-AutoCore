package com.wac.autocore.view;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Customer;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.components.CustomerCard;
import com.wac.autocore.view.components.CustomerDetails;
import com.wac.autocore.view.components.KanbanGridUtil;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ShowCustomersView {
    private final GarageSystem garageSystem;

    public ShowCustomersView() {
        garageSystem = new GarageSystem();
    }

    public Parent show() {
        BorderPane layout = new BorderPane();
        layout.setTop(getHeader());

        GridPane mainContent = KanbanGridUtil.getKanbanGrid(2);

        VBox customersBox = new VBox();

        for (Customer customer : garageSystem.getCustomers()) {
            CustomerCard customerCard = new CustomerCard(customer);
            customerCard.setOnMouseClicked(e -> {

                CustomerDetails customerDetails = new CustomerDetails(customer);
                customerDetails.setMaxHeight(Double.MAX_VALUE);

                mainContent.getChildren().removeIf(node ->
                        GridPane.getColumnIndex(node) != null
                                && GridPane.getColumnIndex(node) == 1);

                mainContent.add(
                        customerDetails,
                        1, 0
                );

                GridPane.setVgrow(customerDetails, Priority.ALWAYS);
            });
            customersBox.getChildren().add(customerCard);
        }

        VBox customersScroll = KanbanGridUtil.getScrollableColumnWithTitle("", customersBox);

        mainContent.add(
                customersScroll,
                0, 0);

        layout.setCenter(mainContent);
        return layout;
    }

    private Node getHeader(){
        BorderPane headerPane = new BorderPane();
        Label title = new Label(LanguageManager.getString("customers.title"));
        title.getStyleClass().setAll("page-title");
        Button createCustomerBtn = new Button(LanguageManager.getString("customers.create"));
        createCustomerBtn.getStyleClass().addAll("create-btn");
        createCustomerBtn.setOnAction(e -> ViewManager.getInstance().showCreateCustomerPopup());
        headerPane.setCenter(title);
        headerPane.setRight(createCustomerBtn);
        return headerPane;
    }
}