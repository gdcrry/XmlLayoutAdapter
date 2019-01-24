package com.example.gdcrry.layoutadapterdemo.layout_adapter.properties;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.example.gdcrry.layoutadapterdemo.layout_adapter.CustomProperties;

/**
 * Created by zhangtianye.bugfree on 2019/1/23.
 */
public class TextProperty extends CustomProperties.StringCustomProperty {

    @NonNull
    @Override
    protected String getPropertyName() {
        return "text";
    }

    @NonNull
    @Override
    protected String getDefaultValue() {
        return "";
    }

    @Override
    protected String parseValue(@NonNull String valueString) throws Exception {
        return valueString;
    }

    @Override
    protected void applyValue(@NonNull View view) {
        if (view instanceof TextView) {
            ((TextView) view).setText(value);
        }
    }
}
