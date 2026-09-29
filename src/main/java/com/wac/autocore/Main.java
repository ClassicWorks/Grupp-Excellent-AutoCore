package com.wac.autocore;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.service.GarageSystem;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        GarageSystem garageSystem = new GarageSystem();

        garageSystem.showWorkOrders();

        ViewManager.getInstance().init(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }

}