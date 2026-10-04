package com.wac.autocore.view;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Customer;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.util.StylingUtil;
import com.wac.autocore.view.components.CustomerCard;
import com.wac.autocore.view.components.CustomerDetails;
import com.wac.autocore.view.components.kanban.KanbanGrid;
import com.wac.autocore.view.components.kanban.KanbanGridUtil;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
        Node header = getHeader();
        header.getStyleClass().add("content-header-container");
        layout.setTop(header);

        GridPane mainContent = new KanbanGrid(2);

        VBox customersBox = new VBox();
        customersBox.getStyleClass().add("card-container");

        for (Customer customer : garageSystem.getCustomers()) {
            CustomerCard customerCard = new CustomerCard(customer);
            customerCard.getStyleClass().add("clickable");
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

                StylingUtil.setSelected(customerCard, "card");

                GridPane.setVgrow(customerDetails, Priority.ALWAYS);
            });
            customersBox.getChildren().add(customerCard);
        }

        ScrollPane customersScroll = new ScrollPane(customersBox);
        customersScroll.setFitToWidth(true);
        customersScroll.setFitToHeight(true);

        GridPane.setVgrow(customersScroll, Priority.ALWAYS);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

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
        createCustomerBtn.getStyleClass().addAll("confirm-btn");
        createCustomerBtn.setOnAction(e -> ViewManager.getInstance().showCreateCustomerPopup());
        headerPane.setCenter(title);
        headerPane.setRight(createCustomerBtn);
        return headerPane;
    }
}