package com.wac.autocore.view.components;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class ImageViewWithAltText extends StackPane {
    public ImageViewWithAltText(String url, double height, double width, String altText) {
        try {
            Image image = new Image(url, width, height, true, false);

            if (image.isError()) {
                Label altLabel = new Label(altText);
                altLabel.getStyleClass().add("alt-label");
                this.getChildren().add(altLabel);

            } else {
                ImageView imageView = new ImageView(image);
                this.getChildren().add(imageView);

            }

        } catch (IllegalArgumentException e) {
            Label altLabel = new Label(altText);
            altLabel.getStyleClass().add("alt-label");
            this.getChildren().add(altLabel);

        }
    }
}
