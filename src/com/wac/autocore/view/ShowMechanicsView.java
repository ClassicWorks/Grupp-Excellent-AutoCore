package com.wac.autocore.view;

import com.wac.autocore.data.Database;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.view.components.MechanicCard;
import com.wac.autocore.view.components.MechanicDetailForm;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class ShowMechanicsView {

    public Parent show() {

        VBox rightPanel = new VBox(10);

        VBox mechanicsBox = new VBox(10);
        ScrollPane mechanicsScroll = new ScrollPane(mechanicsBox);
        mechanicsScroll.setPrefHeight(400);
        mechanicsScroll.setPrefViewportWidth(400);

        List<Mechanic> mechanics = Database.getMechanics();
        for (Mechanic mechanic : mechanics) {
            Node card = MechanicCard.getCard(mechanic, m -> {
                rightPanel.getChildren().setAll(MechanicDetailForm.getForm(m));
            });
            mechanicsBox.getChildren().add(card);
        }

        Button createMechanicBtn = new Button("Create new mechanic");
        createMechanicBtn.getStyleClass().add("create-btn");
        createMechanicBtn.setOnAction(e -> System.out.println("Calling ViewManager.showCreateMechanicPopup()"));

        VBox leftColumn = new VBox(10, mechanicsScroll, createMechanicBtn);

        HBox layout = new HBox(20, leftColumn, rightPanel);

        return layout;
    }
}
