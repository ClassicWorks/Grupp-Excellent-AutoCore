package com.wac.autocore.controller;

import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.components.AppDialog;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        log.error("Unexpected error on thread {}", thread.getName(), throwable);

        Platform.runLater(() -> AppDialog.showError(
                LanguageManager.getString("error.title"),
                LanguageManager.getString("error.unexpected.header"),
                LanguageManager.getString("error.unexpected.message")));
    }
}
