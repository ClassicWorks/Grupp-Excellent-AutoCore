package com.wac.autocore.view.components;

import com.wac.autocore.model.ServiceItem;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.Cursor;

import java.util.function.Consumer;

public class ServiceItemCard extends VBox {

    public ServiceItemCard(ServiceItem serviceItem, Consumer<ServiceItem> onCardClick) {
        Label nameLabel = new Label(serviceItem.getName());

        Label priceAndTimeLabel = new Label(String.format("%.0f kr  |  %d min",
                serviceItem.getPrice(),
                serviceItem.getEstimatedMinutes()));

        this.getChildren().addAll(nameLabel, priceAndTimeLabel);
        this.setSpacing(5);
        this.getStyleClass().addAll("service-item-card", "card");

        //Remove later when CSS-sheet exists.
        this.setStyle("-fx-border-color: blue;");
        
        this.setCursor(Cursor.HAND);

        this.setOnMouseClicked(e -> onCardClick.accept(serviceItem));
    }
}
