package com.example.gdcrry.layoutadapterdemo.layout_adapter.properties;

import android.support.annotation.NonNull;
import android.view.View;

import com.example.gdcrry.layoutadapterdemo.layout_adapter.CustomProperties;

/**
 * Created by zhangtianye.bugfree on 2019/1/24.
 */
public class BackgroundProperty extends CustomProperties.IntegerCustomProperty {

    @NonNull
    @Override
    protected String getPropertyName() {
        return "background";
    }

    @NonNull
    @Override
    protected Integer getDefaultValue() {
        return 0;
    }

    @Override
    protected void applyValue(@NonNull View view) {
        view.setBackgroundResource(value);
    }

    @Override
    protected Integer parseValue(@NonNull String valueString) throws Exception {
        String[] parts = valueString.split("@");
        return super.parseValue(parts[parts.length - 1]);
    }
}
