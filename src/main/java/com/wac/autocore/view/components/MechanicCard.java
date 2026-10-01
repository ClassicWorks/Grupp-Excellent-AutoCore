package com.wac.autocore.view.components;

import com.wac.autocore.model.Mechanic;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class MechanicCard extends HBox {

    public MechanicCard(Mechanic mechanic, Consumer<Mechanic> onCardClick) {
        ImageView mechanicIcon = new IconImageView("/imgs/wrench-solid.png", 40, 40);

        Label nameLabel = new Label(mechanic.getName());
        Label specializationlabel = new Label(mechanic.getSpecialization());
        Label availabilityLabel = new Label(mechanic.isAvailable() ? "Available" : "Busy");

        VBox mechanicInfo = new VBox(nameLabel, specializationlabel, availabilityLabel);

        this.getChildren().addAll(mechanicIcon, mechanicInfo);
        this.setAlignment(Pos.CENTER_LEFT);
        this.getStyleClass().addAll("mechanic-card", "card");

        //Remove later when CSS-sheet exists.
        this.setStyle("-fx-border-color: blue;");

        this.setCursor(Cursor.HAND);

        this.setOnMouseClicked(e -> onCardClick.accept(mechanic));
    }
}
