package com.wac.autocore.view;

import com.wac.autocore.model.Mechanic;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.components.kanban.KanbanGridUtil;
import com.wac.autocore.view.components.MechanicCard;
import com.wac.autocore.view.components.MechanicDetails;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

        layout.setTop(getHeader());

        //Split view, list to the left, details to the right
        GridPane mainContent = KanbanGridUtil.getKanbanGrid(2);

        VBox detailPanel = new VBox();

        VBox mechanicsBox = new VBox();
        List<Mechanic> mechanics = garageSystem.getMechanics();

        if (mechanics.isEmpty()) {
            mechanicsBox.getChildren().add(new Label("No mechanics found."));
        }

        for (Mechanic mechanic : mechanics) {
            MechanicCard card = new MechanicCard(mechanic, m ->
                    detailPanel.getChildren().setAll(new MechanicDetails(m))
                    );
            mechanicsBox.getChildren().add(card);
        }

        VBox mechanicsColumn = KanbanGridUtil.getScrollableColumnWithTitle("", mechanicsBox, "");

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
        createMechanicBtn.getStyleClass().addAll("create-btn");

        createMechanicBtn.setOnAction(e ->
                System.out.println("Should call ViewManager.getInstance().showCreateMechanicPopup()"));

        headerPane.setCenter(title);
        headerPane.setRight(createMechanicBtn);
        return headerPane;
    }
}
