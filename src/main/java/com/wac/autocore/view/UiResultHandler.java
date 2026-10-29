package com.wac.autocore.view;

import com.wac.autocore.controller.Result;
import com.wac.autocore.view.components.AppDialog;

public class UiResultHandler {

    public <T> Result<T> handle(Result<T> result) {
        if (!result.isSuccess()) {
            AppDialog.showErrorMessage(result.error());
        }
        return result;
    }
}
