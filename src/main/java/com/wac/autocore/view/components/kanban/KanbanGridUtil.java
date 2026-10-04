package com.wac.autocore.view.components.kanban;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

@Deprecated
public class KanbanGridUtil {
    @Deprecated
    /**
     * Returns a column with at Node that is scrollable. Title on top. The scrollable node will fill the
     * rest of the height of the parent.
     * @param columnTitle Title of column
     * @param childInScroll Node to be scrollable
     * @return
     */
    public static VBox getScrollableColumnWithTitle(String columnTitle, Node childInScroll, String status) {
        HBox columnHeader = getColumnHeader(columnTitle, status);
        ScrollPane scrollPane = new ScrollPane(childInScroll);

        VBox column = new VBox(columnHeader, scrollPane);
        //Make scrollpane fill column
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        VBox.setVgrow(column, Priority.ALWAYS);
        return column;
    }

    @Deprecated
    public static VBox getScrollableColumnWithTopNode(Node columnHeader, Node childInScroll) {
        ScrollPane scrollPane = new ScrollPane(childInScroll);

        VBox column = new VBox(columnHeader, scrollPane);
        //Make scrollpane fill column
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        VBox.setVgrow(column, Priority.ALWAYS);
        return column;
    }

    @Deprecated
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
        kanbanGrid.getStyleClass().add("grid");
        return kanbanGrid;
    }

    @Deprecated
    private static HBox getColumnHeader(String incomingOrders, String status) {
        if(incomingOrders.trim().isEmpty()){
            return new HBox();
        }
        Label title = new Label(incomingOrders);
        title.getStyleClass().addAll("column-title");

        HBox header = new HBox(title);
        header.getStyleClass().addAll("column-header", status);
        header.setAlignment(Pos.CENTER);
        header.setMaxWidth(Double.MAX_VALUE);
        return header;
    }
}
