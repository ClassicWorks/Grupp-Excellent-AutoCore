package com.wac.autocore.view;

import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.ServicePack;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.components.ServicePackCard;
import com.wac.autocore.view.components.ServicePackDetails;
import com.wac.autocore.view.components.kanban.KanbanGrid;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.components.ServiceItemCard;
import com.wac.autocore.view.components.ServiceItemDetails;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class ShowServiceItemsView {
    private final GarageSystem garageSystem;

    public ShowServiceItemsView() {
        garageSystem = new GarageSystem();
    }

    public Parent show() {
        BorderPane layout = new BorderPane();
        Node header = getHeader();
        header.getStyleClass().add("content-header-container");
        layout.setTop(header);

        GridPane mainContent = new KanbanGrid(2);

        VBox detailPanel = new VBox();

        VBox serviceItemsBox = new VBox();
        fillServiceItems(serviceItemsBox, detailPanel);

        VBox servicePackBox = new VBox();
        fillServicePacks(servicePackBox, detailPanel);

        ScrollPane serviceItemsColumn = new ScrollPane(serviceItemsBox);
        serviceItemsColumn.setFitToWidth(true);
        serviceItemsColumn.setFitToHeight(true);

        ScrollPane servicePackColumn = new ScrollPane(servicePackBox);
        servicePackColumn.setFitToWidth(true);
        servicePackColumn.setFitToHeight(true);

        mainContent.add(serviceItemsColumn, 0, 0);
        mainContent.add(servicePackColumn, 0, 1);
        mainContent.add(detailPanel, 1, 0, 1,2);

        GridPane.setVgrow(serviceItemsColumn, Priority.ALWAYS);
        GridPane.setVgrow(servicePackColumn, Priority.ALWAYS);
        GridPane.setVgrow(detailPanel, Priority.ALWAYS);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        layout.setCenter(mainContent);
        return layout;
    }

    private void fillServiceItems(VBox serviceItemsBox, VBox detailPanel) {
        serviceItemsBox.getStyleClass().add("card-container");
        List<ServiceItem> serviceItems = garageSystem.getServiceItems();
        serviceItemsBox.getChildren().clear();

        if (serviceItems.isEmpty()) {
            Label errorLabel = new Label(LanguageManager.getString("services.empty"));
            errorLabel.getStyleClass().add("error-label");
            serviceItemsBox.getChildren().add(errorLabel);
        }


        for (ServiceItem serviceItem : serviceItems) {
            ServiceItemCard card = new ServiceItemCard(serviceItem, s ->
            {
                ServiceItemDetails details = new ServiceItemDetails(s, () -> fillServiceItems(serviceItemsBox, detailPanel));
                VBox.setVgrow(details, Priority.ALWAYS);
                detailPanel.getChildren().setAll(
                        details);
            }
            );
            card.getStyleClass().add("clickable");
            serviceItemsBox.getChildren().add(card);
        }
    }

    private void fillServicePacks(VBox servicePackBox, VBox detailPanel) {
        servicePackBox.getStyleClass().add("card-container");
        List<ServicePack> servicePacks = garageSystem.getServicePacks();
        servicePackBox.getChildren().clear();

        if (servicePacks.isEmpty()) {
            Label errorLabel = new Label(LanguageManager.getString("services.empty"));
            errorLabel.getStyleClass().add("error-label");
            servicePackBox.getChildren().add(errorLabel);
        }


        for (ServicePack servicePack : servicePacks) {
            ServicePackCard card = new ServicePackCard(servicePack);

            card.setOnMouseClicked(e -> {
                ServicePackDetails servicePackDetails = new ServicePackDetails(servicePack,
                        garageSystem.getServiceItems(),
                        s -> saveServicePack(s),
                        s -> deleteServicePack(s));
                VBox.setVgrow(servicePackDetails, Priority.ALWAYS);

                detailPanel.getChildren().setAll(servicePackDetails);
            });
            card.getStyleClass().add("clickable");
            servicePackBox.getChildren().add(card);
        }
    }

    private void deleteServicePack(ServicePack s) {
        System.out.println("Should delete " + s);
    }

    private void saveServicePack(ServicePack s) {
        System.out.println("Should save servicePack " + s);
    }

    private Node getHeader() {
        BorderPane headerPane = new BorderPane();
        Label title = new Label(LanguageManager.getString("services.title"));
        title.getStyleClass().setAll("page-title");

        headerPane.setCenter(title);
        return headerPane;
    }
}