package com.wac.autocore.controller;

import com.wac.autocore.exceptions.AutoCoreException;
import com.wac.autocore.exceptions.NotAuthenticatedException;
import com.wac.autocore.exceptions.PermissionDeniedException;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.components.AppDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

public class ErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    public boolean run(Runnable action) {
        return call(() -> {
            action.run();
            return true;
        }).isPresent();
    }

    public <T> Optional<T> call(Supplier<T> action) {
        try {
            return Optional.ofNullable(action.get());
        } catch (AutoCoreException e) {
            log.warn("Operation failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            AppDialog.showError(
                    LanguageManager.getString("error.title"),
                    LanguageManager.getString("error.header"),
                    messageFor(e));
            return Optional.empty();
        }
    }

    private String messageFor(AutoCoreException e) {
        if (e instanceof PermissionDeniedException) {
            return LanguageManager.getString("error.permissionDenied");
        }
        if (e instanceof NotAuthenticatedException) {
            return LanguageManager.getString("error.notAuthenticated");
        }
        return e.getMessage();
    }
}
