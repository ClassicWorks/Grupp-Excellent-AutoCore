package com.wac.autocore.controller;

import com.wac.autocore.exceptions.AutoCoreException;
import com.wac.autocore.exceptions.NotAuthenticatedException;
import com.wac.autocore.exceptions.PermissionDeniedException;
import com.wac.autocore.util.LanguageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class ErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    public Result<Void> run(Runnable action) {
        return call(() -> {
            action.run();
            return null;
        });
    }

    public <T> Result<T> call(Supplier<T> action) {
        try {
            return Result.success(action.get());
        } catch (AutoCoreException e) {
            log.warn("Operation failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return Result.failure(messageFor(e));
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
