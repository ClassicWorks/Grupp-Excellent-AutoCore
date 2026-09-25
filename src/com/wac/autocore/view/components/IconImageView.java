package com.wac.autocore.view.components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconImageView extends ImageView {
    public IconImageView(String url, double height, double width){
        this.setImage(new Image(url));
        this.setFitHeight(height);
        this.setFitWidth(width);
    }
}
