package com.wac.autocore.view.components;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FormFieldContainer extends VBox {
    public FormFieldContainer(String labelText, Node field, boolean required) {
        Label label = new Label(labelText + (required ? " (*)" : ""));
        label.getStyleClass().add("form-field-label");
        this.getChildren().addAll(label, field);
        this.getStyleClass().add("form-field-container");
    }
}
