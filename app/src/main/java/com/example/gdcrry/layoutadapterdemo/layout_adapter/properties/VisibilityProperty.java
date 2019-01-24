package com.example.gdcrry.layoutadapterdemo.layout_adapter.properties;

import android.support.annotation.NonNull;
import android.view.View;

import com.example.gdcrry.layoutadapterdemo.layout_adapter.CustomProperties;

/**
 * Created by zhangtianye.bugfree on 2019/1/15.
 */
public class VisibilityProperty extends CustomProperties.IntegerCustomProperty {

    @NonNull
    @Override
    public String getPropertyName() {
        return "visibility";
    }

    @NonNull
    @Override
    protected Integer getDefaultValue() {
        return View.VISIBLE;
    }

    @Override
    protected void applyValue(@NonNull View view) {
        switch (value) {
            case 1:
                view.setVisibility(View.INVISIBLE);
                break;
            case 2:
                view.setVisibility(View.GONE);
                break;
            case 0:
            default:
                view.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected boolean setByDefault() {
        return true;
    }
}
