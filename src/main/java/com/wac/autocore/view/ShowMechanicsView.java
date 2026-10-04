package com.wac.autocore.view;

import com.wac.autocore.model.Mechanic;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.StylingUtil;
import com.wac.autocore.view.components.kanban.KanbanGrid;
import com.wac.autocore.view.components.kanban.KanbanGridUtil;
import com.wac.autocore.view.components.MechanicCard;
import com.wac.autocore.view.components.MechanicDetails;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class ShowMechanicsView {
    private final GarageSystem garageSystem;

    public ShowMechanicsView() {
        garageSystem = new GarageSystem();
    }

    public Parent show() {
        BorderPane layout = new BorderPane();

        Node header = getHeader();
        header.getStyleClass().add("content-header-container");
        layout.setTop(header);

        //Split view, list to the left, details to the right
        GridPane mainContent = new KanbanGrid(2);

        VBox detailPanel = new VBox();

        VBox mechanicsBox = new VBox();
        mechanicsBox.getStyleClass().add("card-container");

        List<Mechanic> mechanics = garageSystem.getMechanics();

        if (mechanics.isEmpty()) {
            Label errorLabel = new Label("No mechanics found.");
            errorLabel.getStyleClass().add("error-label");
            mechanicsBox.getChildren().add(errorLabel);
        }

        for (Mechanic mechanic : mechanics) {
            MechanicCard card = new MechanicCard(mechanic, m -> {
                MechanicDetails mechanicDetails = new MechanicDetails(m);
                detailPanel.getChildren().setAll(mechanicDetails);
                VBox.setVgrow(mechanicDetails, Priority.ALWAYS);
            }
                    );
            mechanicsBox.getChildren().add(card);
        }

        ScrollPane mechanicsColumn = new ScrollPane(mechanicsBox);
        mechanicsColumn.setFitToWidth(true);
        mechanicsColumn.setFitToHeight(true);

        mainContent.add(mechanicsColumn, 0, 0);
        mainContent.add(detailPanel, 1, 0);

        GridPane.setVgrow(mechanicsColumn, Priority.ALWAYS);
        GridPane.setVgrow(detailPanel, Priority.ALWAYS);

        layout.setCenter(mainContent);
        return layout;
    }

    private Node getHeader() {
        BorderPane headerPane = new BorderPane();
        Label title = new Label("Mechanics");
        title.getStyleClass().setAll("page-title");

        Button createMechanicBtn = new Button("Create new mechanic");
        createMechanicBtn.getStyleClass().addAll("confirm-btn");

        createMechanicBtn.setOnAction(e ->
                System.out.println("Should call ViewManager.getInstance().showCreateMechanicPopup()"));

        headerPane.setCenter(title);
        headerPane.setRight(createMechanicBtn);
        return headerPane;
    }
}
