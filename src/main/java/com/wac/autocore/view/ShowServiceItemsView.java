package com.wac.autocore.view;

import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.components.KanbanGridUtil;
import com.wac.autocore.view.components.ServiceItemCard;
import com.wac.autocore.view.components.ServiceItemDetails;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
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
        layout.setTop(getHeader());

        GridPane mainContent = KanbanGridUtil.getKanbanGrid(2);

        VBox detailPanel = new VBox();

        VBox serviceItemsBox = new VBox();
        fillServiceItems(serviceItemsBox, detailPanel);

        VBox serviceItemsColumn = KanbanGridUtil.getScrollableColumnWithTitle("", serviceItemsBox);

        mainContent.add(serviceItemsColumn, 0, 0);
        mainContent.add(detailPanel, 1, 0);

        GridPane.setVgrow(serviceItemsColumn, Priority.ALWAYS);
        GridPane.setVgrow(detailPanel, Priority.ALWAYS);

        layout.setCenter(mainContent);
        return layout;
    }

    private void fillServiceItems(VBox serviceItemsBox, VBox detailPanel) {
        serviceItemsBox.getChildren().clear();

        List<ServiceItem> serviceItems = garageSystem.getServiceItems();

        if (serviceItems.isEmpty()) {
            serviceItemsBox.getChildren().add(new Label(LanguageManager.getString("services.empty")));
        }

        for (ServiceItem serviceItem : serviceItems) {
            ServiceItemCard card = new ServiceItemCard(serviceItem, s ->
                    detailPanel.getChildren().setAll(
                            new ServiceItemDetails(s, () -> fillServiceItems(serviceItemsBox, detailPanel)))
            );
            serviceItemsBox.getChildren().add(card);
        }
    }

    private Node getHeader() {
        BorderPane headerPane = new BorderPane();
        Label title = new Label(LanguageManager.getString("services.title"));
        title.getStyleClass().setAll("page-title");

        headerPane.setCenter(title);
        return headerPane;
    }
}