package com.wac.autocore;

import com.wac.autocore.config.ManualAppContext;
import com.wac.autocore.data.UserRepoImpl;
import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.security.PasswordHasher;
import com.wac.autocore.security.UserSeeder;
import com.wac.autocore.util.HibernateUtil;
import com.wac.autocore.util.LanguageManager;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage stage) {
        ViewManager viewManager = ViewManager.getInstance();
        viewManager.init(stage, new ManualAppContext().garageSystem());
        viewManager.showView(createLoadingView());

        Task<Void> loadDatabase = new Task<Void>() {
            @Override
            protected Void call() {
                HibernateUtil.getSessionFactory();
                new UserSeeder(new UserRepoImpl(), new PasswordHasher()).seedTestUsers();
                return null;
            }
        };

        loadDatabase.setOnSucceeded(e -> viewManager.showVehicles());
        loadDatabase.setOnFailed(e -> viewManager.showView(createErrorView()));

        Thread loadingThread = new Thread(loadDatabase);
        loadingThread.setDaemon(true);
        loadingThread.start();
    }

    private Parent createLoadingView() {
        ProgressIndicator indicator = new ProgressIndicator();
        Label label = new Label(LanguageManager.getString("loading.database"));

        VBox box = new VBox(15, indicator, label);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private Parent createErrorView() {
        Label label = new Label(LanguageManager.getString("loading.error"));
        label.getStyleClass().add("error-label");
        label.setWrapText(true);

        VBox box = new VBox(label);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    public static void main(String[] args) {
        launch(args);
    }
}