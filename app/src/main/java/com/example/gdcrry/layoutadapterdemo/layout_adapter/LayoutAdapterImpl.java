package com.example.gdcrry.layoutadapterdemo.layout_adapter;

import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.transition.TransitionManager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by zhangtianye.bugfree on 2019/1/15.
 */
class LayoutAdapterImpl {

    private static final int NONE_ID_VALUE = -1;

    private static volatile LayoutAdapterImpl instance;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private SparseArray<SparseArray<ViewInfoData>> cachedSparseArray = new SparseArray<>();

    private LayoutAdapterImpl() {
    }

    static LayoutAdapterImpl getInstance() {
        if (instance == null) {
            synchronized (LayoutAdapterImpl.class) {
                if (instance == null) {
                    instance = new LayoutAdapterImpl();
                }
            }
        }
        return instance;
    }

    void apply(LayoutAdapter.Config config) {
        Runnable task = () -> {
            XmlResourceParser parser = config.resources.getLayout(config.layoutId);
            SparseArray<ViewInfoData> sparseArray;
            sparseArray = cachedSparseArray.get(config.layoutId);
            if (sparseArray == null) {
                sparseArray = new SparseArray<>();
                try {
                    generate(config, config.parent, parser, sparseArray);
                } catch (XmlPullParserException | IOException e) {
                    if (config.errorHandler != null) {
                        config.errorHandler.onException(e);
                    }
                }
                putToCache(config, sparseArray);
            }
            SparseArray<ViewInfoData> finalSparseArray = sparseArray;
            if (Looper.myLooper() != Looper.getMainLooper()) {
                mainHandler.post(() -> {
                    animateApplyToViews(config, finalSparseArray);
                    if (config.callback != null) {
                        config.callback.run();
                    }
                });
            } else {
                animateApplyToViews(config, finalSparseArray);
                if (config.callback != null) {
                    config.callback.run();
                }
            }
        };

        if (config.async) {
            config.asyncExecutor.execute(task);
        } else {
            task.run();
        }
    }

    static void release() {
        instance = null;
    }

    private void generate(LayoutAdapter.Config config, ViewGroup parent, XmlResourceParser parser, SparseArray<ViewInfoData> sparseArray)
            throws XmlPullParserException, IOException {
        int state;
        int level = 1;
        do {
            state = parser.next();
            switch (state) {
                case XmlPullParser.START_DOCUMENT:
                    int id = getIdAttributeResourceValue(parser);
                    if (id > 0) {
                        ViewInfoData data = new ViewInfoData();
                        try {
                            data.customProperties.generateCustomProperties(parser);
                        } catch (Exception e) {
                            if (config.errorHandler != null) {
                                config.errorHandler.onException(e);
                            }
                        }
                        sparseArray.put(id, data);
                    }
                    break;
                case XmlPullParser.START_TAG:
                    int childId = getIdAttributeResourceValue(parser);
                    if (childId > 0) {
                        ViewInfoData data = new ViewInfoData();
                        data.layoutParams = parent.generateLayoutParams(parser);
                        try {
                            data.customProperties.generateCustomProperties(parser);
                        } catch (Exception e) {
                            if (config.errorHandler != null) {
                                config.errorHandler.onException(e);
                            }
                        }
                        sparseArray.put(childId, data);
                        try {
                            View newParent = parent.findViewById(childId);
                            if (newParent instanceof ViewGroup) {
                                generate(config, (ViewGroup) newParent, parser, sparseArray);
                                level--;
                            }
                        } catch (Exception e) {
                            if (config.errorHandler != null) {
                                config.errorHandler.onException(e);
                            }
                        }
                    }
                    level++;
                    break;
                case XmlPullParser.END_TAG:
                    if (--level <= 0) {
                        return;
                    }
                    break;
                default:
            }
        } while (state != XmlPullParser.END_DOCUMENT);
    }

    private void applyToViews(ViewGroup parent, SparseArray<ViewInfoData> sparseArray) {
        ViewInfoData parentData = sparseArray.get(parent.getId());
        if (parentData != null) {
            parentData.customProperties.applyAll(parent);
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            ViewInfoData data = sparseArray.get(child.getId());
            if (data != null) {
                child.setLayoutParams(data.layoutParams);
                data.customProperties.applyAll(child);
            }
            if (child instanceof ViewGroup) {
                applyToViews((ViewGroup) child, sparseArray);
            }
        }
    }

    private void putToCache(LayoutAdapter.Config config, SparseArray<ViewInfoData> data) {
        cachedSparseArray.put(config.layoutId, data);
    }

    private void animateApplyToViews(LayoutAdapter.Config config, SparseArray<ViewInfoData> sparseArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && config.animate) {
            TransitionManager.beginDelayedTransition(config.parent, config.transition);
        }
        applyToViews(config.parent, sparseArray);
    }

    private static int getIdAttributeResourceValue(XmlResourceParser parser) {
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String attrName = parser.getAttributeName(i);
            if ("id".equalsIgnoreCase(attrName)) {
                return parser.getAttributeResourceValue(i, NONE_ID_VALUE);
            }
        }
        return NONE_ID_VALUE;
    }

    private static class ViewInfoData {
        private ViewGroup.LayoutParams layoutParams;
        private CustomProperties customProperties = new CustomProperties();
    }
}
