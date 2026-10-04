package com.wac.autocore.view.components;

import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.util.StylingUtil;
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
        this.getStyleClass().addAll("card");

        this.setOnMouseClicked(e -> {
            StylingUtil.setSelected(this, "card");
            onCardClick.accept(serviceItem);
        });
    }
}
