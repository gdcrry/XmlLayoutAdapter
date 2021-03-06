package com.example.gdcrry.layoutadapterdemo.layout_adapter.properties;

import android.support.annotation.NonNull;
import android.view.View;

import com.example.gdcrry.layoutadapterdemo.layout_adapter.CustomProperties;

/**
 * Created by zhangtianye.bugfree on 2019/1/24.
 */
public class RotationProperty extends CustomProperties.FloatCustomProperty {
    @NonNull
    @Override
    protected String getPropertyName() {
        return "rotation";
    }

    @NonNull
    @Override
    protected Float getDefaultValue() {
        return 0.f;
    }

    @Override
    protected void applyValue(@NonNull View view) {
        view.setRotation(value);
    }

    @Override
    protected boolean setByDefault() {
        return true;
    }
}
