package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.util.LanguageManager;
import javafx.scene.control.Hyperlink;

public class MechanicHyperLink extends Hyperlink {
    private Mechanic mechanic;

    public void setMechanic(Mechanic mechanic) {
        this.mechanic = mechanic;

        if (mechanic == null){
            setText(LanguageManager.getString("mechanic.none.assigned"));
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
        ImageViewWithAltText wrenchIcon = new ImageViewWithAltText("/imgs/wrench-solid.png", 20, 20,
                LanguageManager.getString("image.wrench-solid"));
        this.setGraphic(wrenchIcon);

        if (mechanic == null){
            this.setText(LanguageManager.getString("mechanic.none.assigned"));
            this.setDisable(true);
        }else {
            this.setText(mechanic.getName());
            this.setOnAction(e ->
                    ViewManager.getInstance().showMechanics()
            );
        }
    }
}