package com.example.gdcrry.layoutadapterdemo.layout_adapter.properties;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;

import com.example.gdcrry.layoutadapterdemo.layout_adapter.CustomProperties;

/**
 * Created by zhangtianye.bugfree on 2019/1/16.
 */
public class OrientationProperty extends CustomProperties.IntegerCustomProperty {

    @NonNull
    @Override
    protected String getPropertyName() {
        return "orientation";
    }

    @NonNull
    @Override
    protected Integer getDefaultValue() {
        return LinearLayout.HORIZONTAL;
    }

    @Override
    protected void applyValue(@NonNull View view) {
        if (view instanceof LinearLayout) {
            switch (value) {
                default:
                case 0:
                    ((LinearLayout) view).setOrientation(LinearLayout.HORIZONTAL);
                    break;
                case 1:
                    ((LinearLayout) view).setOrientation(LinearLayout.VERTICAL);
                    break;
            }
        }
    }
}
