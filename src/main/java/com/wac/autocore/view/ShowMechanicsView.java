package com.wac.autocore.view;

import com.wac.autocore.controller.MechanicController;
import com.wac.autocore.controller.Result;
import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.components.AppDialog;
import com.wac.autocore.view.components.MechanicCard;
import com.wac.autocore.view.components.MechanicDetails;
import com.wac.autocore.view.components.kanban.KanbanGrid;
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
    private final MechanicController mechanicController;
    private final UiResultHandler resultHandler = new UiResultHandler();
    private final VBox detailPanel = new VBox();
    private final VBox mechanicsBox = new VBox();

    public ShowMechanicsView() {
        mechanicController = ViewManager.getInstance().getControllers().mechanics();
    }

    public Parent show() {
        BorderPane layout = new BorderPane();

        Node header = getHeader();
        header.getStyleClass().add("content-header-container");
        layout.setTop(header);

        //Split view, list to the left, details to the right
        GridPane mainContent = new KanbanGrid(2);

        mechanicsBox.getStyleClass().add("card-container");

        fillMechanics();

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

    private void fillMechanics() {
        mechanicsBox.getChildren().clear();

        Result<List<Mechanic>> result = resultHandler.handle(mechanicController.getMechanics());
        if (!result.isSuccess()) {
            return;
        }
        List<Mechanic> mechanics = result.value();

        if (mechanics.isEmpty()) {
            Label errorLabel = new Label(LanguageManager.getString("mechanics.empty"));
            errorLabel.getStyleClass().add("error-label");
            mechanicsBox.getChildren().add(errorLabel);
        }

        for (Mechanic mechanic : mechanics) {
            MechanicCard card = new MechanicCard(mechanic, m -> {
                MechanicDetails mechanicDetails = new MechanicDetails(
                        m,
                        this::fillMechanics,
                        this::deleteMechanic);
                detailPanel.getChildren().setAll(mechanicDetails);
                VBox.setVgrow(mechanicDetails, Priority.ALWAYS);
            });
            mechanicsBox.getChildren().add(card);
        }
    }

    private void deleteMechanic(int mechanicId) {
        Result<Boolean> blockedResult =
                resultHandler.handle(mechanicController.mechanicHasUncompletedWorkOrders(mechanicId));
        if (!blockedResult.isSuccess()) {
            return;
        }

        if (blockedResult.value()) {
            AppDialog.showError(
                    LanguageManager.getString("mechanic.delete.title"),
                    LanguageManager.getString("mechanic.delete.blocked.title"),
                    LanguageManager.getString("mechanic.delete.blocked.message")
            );
            return;
        }

        boolean deleteConfirmed = AppDialog.showConfirm(
                LanguageManager.getString("mechanic.delete.title"),
                String.format(
                        LanguageManager.getString("mechanic.delete.message"),
                        mechanicId
                ),
                LanguageManager.getString("mechanic.delete.warning")
        );

        if (!deleteConfirmed) {
            return;
        }

        Result<Boolean> deleteResult = resultHandler.handle(mechanicController.deleteMechanic(mechanicId));
        if (!deleteResult.isSuccess()) {
            return;
        }

        if (!deleteResult.value()) {
            AppDialog.showError(
                    LanguageManager.getString("mechanic.delete.title"),
                    LanguageManager.getString("mechanic.delete.failed"),
                    ""
            );
            return;
        }

        detailPanel.getChildren().clear();
        fillMechanics();
    }

    private Node getHeader() {
        BorderPane headerPane = new BorderPane();
        Label title = new Label(LanguageManager.getString("mechanics.title"));
        title.getStyleClass().setAll("page-title");

        Button createMechanicBtn = new Button(LanguageManager.getString("mechanics.create"));
        createMechanicBtn.getStyleClass().addAll("confirm-btn");

        createMechanicBtn.setOnAction(e ->
                System.out.println("Should call ViewManager.getInstance().showCreateMechanicPopup()"));

        headerPane.setCenter(title);
        headerPane.setRight(createMechanicBtn);
        return headerPane;
    }
}