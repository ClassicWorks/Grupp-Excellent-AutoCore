package com.wac.autocore.view.components;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class KanbanGridUtil {
    /**
     * Returns a column with at Node that is scrollable. Title on top. The scrollable node will fill the
     * rest of the height of the parent.
     * @param columnTitle Title of column
     * @param childInScroll Node to be scrollable
     * @return
     */
    public static VBox getScrollableColumnWithTitle(String columnTitle, Node childInScroll) {
        Label columnHeader = getColumnHeader(columnTitle);
        ScrollPane scrollPane = new ScrollPane(childInScroll);
        scrollPane.setFitToWidth(true);

        VBox column = new VBox(columnHeader, scrollPane);
        //Make scrollpane fill column
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return column;
    }

    public static VBox getScrollableColumnWithTopNode(Node columnHeader, Node childInScroll) {
        ScrollPane scrollPane = new ScrollPane(childInScroll);
        scrollPane.setFitToWidth(true);

        VBox column = new VBox(columnHeader, scrollPane);
        //Make scrollpane fill column
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return column;
    }

    /**
     * Return a GridPane with a set number of columns. The Grid fills the parent's width.
     * The columns will all be equal widths
     * @param numberOfColumns A set number of columns
     * @return GridPane with aset number of columns that are equally wide
     */
    public static GridPane getKanbanGrid(int numberOfColumns) {
        GridPane kanbanGrid = new GridPane();

        for(int i = 0; i < numberOfColumns; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0/numberOfColumns);
            kanbanGrid.getColumnConstraints().add(columnConstraints);
        }
        return kanbanGrid;
    }


    private static Label getColumnHeader(String incomingOrders) {
        Label columnHeader = new Label(incomingOrders);
        columnHeader.getStyleClass().add("column-heder");
        columnHeader.setAlignment(Pos.CENTER);
        columnHeader.setMaxWidth(Double.MAX_VALUE);
        return columnHeader;
    }
}
