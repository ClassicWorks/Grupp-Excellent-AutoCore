package com.wac.autocore.view.components;

import com.wac.autocore.model.Mechanic;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.util.StylingUtil;
import com.wac.autocore.util.ValueLabels;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class MechanicCard extends HBox {

    public MechanicCard(Mechanic mechanic, Consumer<Mechanic> onCardClick) {
        ImageViewWithAltText mechanicIcon = new ImageViewWithAltText("/imgs/wrench-solid.png", 40, 40,
                LanguageManager.getString("image.wrench-solid"));

        Label nameLabel = new Label(mechanic.getName());
        Label specializationlabel = new Label(ValueLabels.specialization(mechanic.getSpecialization()));
        Label availabilityLabel = new Label(mechanic.isAvailable()
                ? LanguageManager.getString("mechanic.status.available")
                : LanguageManager.getString("mechanic.status.busy"));

        VBox mechanicInfo = new VBox(nameLabel, specializationlabel, availabilityLabel);
        mechanicInfo.getStyleClass().addAll("card-info-box");

        this.getChildren().addAll(mechanicIcon, mechanicInfo);
        this.setAlignment(Pos.CENTER_LEFT);
        this.getStyleClass().addAll("card", "clickable");

        this.setOnMouseClicked(e -> {
            StylingUtil.setSelected(this, "card");
            onCardClick.accept(mechanic);
        });
    }
}