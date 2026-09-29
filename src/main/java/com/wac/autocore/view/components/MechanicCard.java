package com.wac.autocore.view.components;

import com.wac.autocore.model.Mechanic;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class MechanicCard {

    public static Node getCard(Mechanic mechanic, Consumer<Mechanic> onCardClick) {

        VBox card = new VBox(5);
        card.setStyle("-fx-border-color: gray; -fx-padding: 10;");

        Label nameLabel = new Label(mechanic.getName());
        Label specializationLabel = new Label(mechanic.getSpecialization());

        card.getChildren().addAll(nameLabel, specializationLabel);

        card.setOnMouseClicked((MouseEvent event) -> onCardClick.accept(mechanic));

        return card;
    }
}
