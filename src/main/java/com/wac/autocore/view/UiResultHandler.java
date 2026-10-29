package com.wac.autocore.view;

import com.wac.autocore.controller.Result;
import com.wac.autocore.view.components.AppDialog;

import java.util.function.Supplier;

public class UiResultHandler {

    public Runnable handle(Supplier<Result<?>> action) {
        return () -> {
            Result<?> result = action.get();
            if (!result.isSuccess()) {
                AppDialog.showErrorMessage(result.error());
            }
        };
    }
}
