package com.example.gdcrry.layoutadapterdemo.layout_adapter.properties;

import android.support.annotation.NonNull;
import android.view.View;

import com.example.gdcrry.layoutadapterdemo.layout_adapter.CustomProperties;

/**
 * Created by zhangtianye.bugfree on 2019/1/23.
 */
public class AlphaProperty extends CustomProperties.FloatCustomProperty {

    @NonNull
    @Override
    protected String getPropertyName() {
        return "alpha";
    }

    @NonNull
    @Override
    protected Float getDefaultValue() {
        return 1.f;
    }

    @Override
    protected void applyValue(@NonNull View view) {
        view.setAlpha(value);
    }

    @Override
    protected boolean setByDefault() {
        return true;
    }
}
