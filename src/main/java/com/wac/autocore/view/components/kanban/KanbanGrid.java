package com.wac.autocore.view.components.kanban;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class KanbanGrid extends GridPane {
    /**
     * Return a GridPane with a set number of columns. The Grid fills the parent's width.
     * The columns will all be equal widths
     * @param numberOfColumns A set number of columns
     * @return GridPane with aset number of columns that are equally wide
     */
    public KanbanGrid(int numberOfColumns) {

        for(int i = 0; i < numberOfColumns; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0/numberOfColumns);

            getColumnConstraints().add(columnConstraints);
        }
        getStyleClass().add("grid");
    }
}
