package com.wac.autocore.view.components.kanban;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ColumnHeader extends HBox {
    public ColumnHeader(String title, String status) {

        Label label = new Label(title);
        label.getStyleClass().add("column-title");

        getChildren().add(label);

        getStyleClass().addAll("column-header", status);

        setMaxWidth(Double.MAX_VALUE);
    }
}
