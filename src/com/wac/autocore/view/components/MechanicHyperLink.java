package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Mechanic;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.ImageView;

public class MechanicHyperLink extends Hyperlink {
    private Mechanic mechanic;

    public void setMechanic(Mechanic mechanic) {
        this.mechanic = mechanic;

        if (mechanic == null){
            setText("No mechanic assigned");
            setDisable(true);
        }else {
            setText(mechanic.getName());
            setDisable(false);
            setOnAction(e ->
                    ViewManager.getInstance().showMechanics()
            );
        }
    }

    public MechanicHyperLink(Mechanic mechanic) {
        this.mechanic = mechanic;
        //TODO change to component
        ImageView imageView = new ImageView("resources/imgs/wrench-solid.png");
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        this.setGraphic(imageView);

        if (mechanic == null){
            setText("No mechanic assigned");
            setDisable(true);
        }else {
            setText(mechanic.getName());
            setOnAction(e ->
                    ViewManager.getInstance().showMechanics()
            );
        }
    }
    public MechanicHyperLink(){
        setText("No mechanic assigned");
        setDisable(true);
    }
}
