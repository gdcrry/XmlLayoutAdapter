package com.example.gdcrry.layoutadapterdemo.layout_adapter;

import android.content.res.XmlResourceParser;
import android.support.annotation.NonNull;
import android.view.View;

import com.example.gdcrry.layoutadapterdemo.layout_adapter.properties.AlphaProperty;
import com.example.gdcrry.layoutadapterdemo.layout_adapter.properties.OrientationProperty;
import com.example.gdcrry.layoutadapterdemo.layout_adapter.properties.RotationProperty;
import com.example.gdcrry.layoutadapterdemo.layout_adapter.properties.TextProperty;
import com.example.gdcrry.layoutadapterdemo.layout_adapter.properties.VisibilityProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangtianye.bugfree on 2019/1/15.
 */
public final class CustomProperties {

    public abstract static class IntegerCustomProperty extends CustomProperty<Integer> {

        @Override
        protected Integer parseValue(@NonNull String valueString) throws Exception {
            return Integer.parseInt(valueString);
        }
    }

    public abstract static class FloatCustomProperty extends CustomProperty<Float> {

        @Override
        protected Float parseValue(@NonNull String valueString) throws Exception {
            return Float.parseFloat(valueString);
        }
    }

    public abstract static class StringCustomProperty extends CustomProperty<String> {

        @Override
        protected String parseValue(@NonNull String valueString) throws Exception {
            return valueString;
        }
    }

    private Map<String, CustomProperty> customPropertyMap = new HashMap<>();

    CustomProperties() {
        registerProperty(new VisibilityProperty());
        registerProperty(new AlphaProperty());
        registerProperty(new OrientationProperty());
        registerProperty(new RotationProperty());
        registerProperty(new TextProperty());
    }

    void generateCustomProperties(XmlResourceParser parser) throws Exception {
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            CustomProperty property = customPropertyMap.get(parser.getAttributeName(i));
            if (property != null) {
                String valueString = parser.getAttributeValue(i);
                if (valueString != null) {
                    property.value = property.parseValue(valueString);
                }
            }
        }
    }

    void applyAll(View view) {
        for (CustomProperty property : customPropertyMap.values()) {
            property.apply(view);
        }
    }

    private void registerProperty(CustomProperty property) {
        customPropertyMap.put(property.getPropertyName(), property);
    }

    private abstract static class CustomProperty<T> {

        protected T value;

        /**
         * XML 中的属性名
         */
        @NonNull
        protected abstract String getPropertyName();

        /**
         * 默认值, 当 setByDefault() 返回 true 时将应用此值
         */
        @NonNull
        protected abstract T getDefaultValue();

        /**
         * 将 XML 中解析的值转换为对应的 Value. 最常用的如 value = Integer.parseInt(valueString);
         *
         * @param valueString 解出的字符串
         * @throws Exception NumberFormatException 等
         */
        protected abstract T parseValue(@NonNull String valueString) throws Exception;

        /**
         * 将 value 设置到 View 上
         *
         * @param view 有些字段设置前需要判断 View 的类型
         */
        protected abstract void applyValue(@NonNull View view);

        /**
         * 当 XML 中没有此字段时是否应用默认值
         *
         * @return 是否应用默认值, 默认不应用
         */
        protected boolean setByDefault() {
            return false;
        }

        private void apply(@NonNull View view) {
            if (value == null) {
                if (!setByDefault()) {
                    return;
                } else {
                    value = getDefaultValue();
                }
            }
            applyValue(view);
        }
    }
}
