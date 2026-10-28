package com.wac.autocore;

import com.wac.autocore.config.AppContext;
import com.wac.autocore.config.ManualAppContext;
import com.wac.autocore.config.SpringAppContext;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main extends Application {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private AppContext appContext;

    @Override
    public void start(Stage stage) {
        boolean standalone = "standalone".equalsIgnoreCase(System.getenv("AUTOCORE_MODE"));

        ViewManager viewManager = ViewManager.getInstance();
        viewManager.init(stage);
        viewManager.showView(createLoadingView());

        Task<AppContext> startup = new Task<>() {
            @Override
            protected AppContext call() {
                log.info("Starting in {} mode", standalone ? "standalone (no Spring)" : "Spring");
                AppContext context = standalone ? new ManualAppContext() : new SpringAppContext();

                HibernateUtil.getSessionFactory();
                new UserSeeder(new UserRepoImpl(), new PasswordHasher()).seedTestUsers();
                return context;
            }
        };

        startup.setOnSucceeded(e -> {
            appContext = startup.getValue();
            viewManager.setGarageSystem(appContext.garageSystem());
            viewManager.setAuthenticator(appContext.authenticator());
            viewManager.setCurrentUserProvider(appContext.currentUserProvider());
            viewManager.showLogin();
        });
        startup.setOnFailed(e -> {
            log.error("Startup failed", startup.getException());
            viewManager.showView(createErrorView());
        });

        Thread startupThread = new Thread(startup);
        startupThread.setDaemon(true);
        startupThread.start();
    }

    @Override
    public void stop() {
        if (appContext != null) {
            appContext.close();
        }
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