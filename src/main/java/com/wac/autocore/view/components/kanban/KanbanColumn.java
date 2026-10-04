package com.wac.autocore.view.components.kanban;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class KanbanColumn extends VBox {
    public KanbanColumn(String title, Node content, String status) {

        ColumnHeader header = new ColumnHeader(title, status);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(header, scrollPane);

        getStyleClass().add("kanban-column");
    }


    public KanbanColumn(Node header, Node content) {

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(header, scrollPane);

        getStyleClass().add("kanban-column");
    }

}
