package com.wac.autocore.view.components;

import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.ServicePack;
import com.wac.autocore.util.LanguageManager;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class ServicePackCard extends VBox {
    public ServicePackCard(ServicePack servicePack){
        Label nameLabel = new Label(servicePack.getName());
        nameLabel.getStyleClass().add("card-title");

        VBox serviceItemBox = new VBox();
        serviceItemBox.getStyleClass().add("card-info-box");

        List<ServiceItem> serviceItems = servicePack.getServiceItems();
        for(ServiceItem serviceItem: serviceItems){
            serviceItemBox.getChildren().add(createServiceItemRow(serviceItem));
        }

        double totalPrice = serviceItems.stream()
                .mapToDouble(ServiceItem::getPrice)
                .sum();
        int totalTime = serviceItems.stream()
                .mapToInt(ServiceItem::getEstimatedMinutes)
                .sum();

        Label totalTimeLabel = new Label(Integer.toString(totalTime) + " min",
                new ImageViewWithAltText("imgs/clock-solid.png", 20, 20,
                        LanguageManager.getString("image.clock-solid"))
        );
        Label totalPriceLabel = new Label(Double.toString(totalPrice) + " SEK");
        HBox totalRow = new HBox(totalTimeLabel, totalPriceLabel);
        totalRow.getStyleClass().addAll("row", "row-final");
        serviceItemBox.getChildren().add(totalRow);

        this.getChildren().addAll(
                nameLabel,
                serviceItemBox
        );
        this.getStyleClass().addAll("card");
    }

    private HBox createServiceItemRow(ServiceItem serviceItem) {
        Label nameLabel = new Label(serviceItem.getName());
        Label priceLabel = new Label(Double.toString(serviceItem.getPrice()));

        HBox row = new HBox(nameLabel, priceLabel);
        row.getStyleClass().add("row");
        HBox.setHgrow(nameLabel, Priority.ALWAYS);
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        return row;
    }
}
