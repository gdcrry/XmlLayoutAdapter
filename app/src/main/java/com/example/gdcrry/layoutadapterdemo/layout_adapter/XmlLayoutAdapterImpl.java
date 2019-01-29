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
class XmlLayoutAdapterImpl {

    private static final int NONE_ID_VALUE = -1;

    private static volatile XmlLayoutAdapterImpl instance;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private SparseArray<SparseArray<ViewInfoData>> cachedSparseArray = new SparseArray<>();

    private XmlLayoutAdapterImpl() {
    }

    static XmlLayoutAdapterImpl getInstance() {
        if (instance == null) {
            synchronized (XmlLayoutAdapterImpl.class) {
                if (instance == null) {
                    instance = new XmlLayoutAdapterImpl();
                }
            }
        }
        return instance;
    }

    void apply(XmlLayoutAdapter.Config config) {
        Runnable task = () -> {
            XmlResourceParser parser = config.resources.getLayout(config.layoutId);
            SparseArray<ViewInfoData> sparseArray;
            sparseArray = cachedSparseArray.get(config.layoutId);
            if (sparseArray == null) {
                sparseArray = new SparseArray<>();
                try {
                    generate(config, config.parent, parser, sparseArray, 0);
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

    private boolean generate(XmlLayoutAdapter.Config config, ViewGroup parent, XmlResourceParser parser, SparseArray<ViewInfoData> sparseArray, int level)
            throws XmlPullParserException, IOException {
        int state;
        int minLevel = level;
        boolean hasChild = false;
        do {
            state = parser.next();
            switch (state) {
                case XmlPullParser.START_TAG:
                    int childId = getIdAttributeResourceValue(parser);
                    if (childId > 0) {
                        hasChild = true;
                        ViewInfoData data = new ViewInfoData();
                        if (level > 0) {
                            data.layoutParams = parent.generateLayoutParams(parser);
                        }
                        try {
                            data.customProperties.generateCustomProperties(parser);
                        } catch (Exception e) {
                            if (config.errorHandler != null) {
                                config.errorHandler.onException(e);
                            }
                        }
                        try {
                            View newParent = parent.findViewById(childId);
                            if (shouldGenerateChild(newParent)) {
                                data.hasChild = generate(config, (ViewGroup) newParent, parser, sparseArray, level + 1);
                                level--;
                            }
                        } catch (Exception e) {
                            if (config.errorHandler != null) {
                                config.errorHandler.onException(e);
                            }
                        }
                        sparseArray.put(childId, data);
                    }
                    level++;
                    break;
                case XmlPullParser.END_TAG:
                    if (--level < minLevel) {
                        return hasChild;
                    }
                    break;
                default:
            }
        } while (state != XmlPullParser.END_DOCUMENT);
        return false;
    }

    private void applyToViews(XmlLayoutAdapter.Config config, ViewGroup parent, SparseArray<ViewInfoData> sparseArray) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            int id = child.getId();
            if (id <= 0 || config.ignoreChildIdSet.contains(id)) {
                continue;
            }
            ViewInfoData data = sparseArray.get(child.getId());
            if (data != null) {
                child.setLayoutParams(data.layoutParams);
                data.customProperties.applyAll(child);
                if (data.hasChild) {
                    applyToViews(config, (ViewGroup) child, sparseArray);
                }
            }
        }
    }

    private void putToCache(XmlLayoutAdapter.Config config, SparseArray<ViewInfoData> data) {
        cachedSparseArray.put(config.layoutId, data);
    }

    private void animateApplyToViews(XmlLayoutAdapter.Config config, SparseArray<ViewInfoData> sparseArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && config.animate) {
            if (config.transition != null) {
                for (int i = 0; i < sparseArray.size(); i++) {
                    config.transition.addTarget(sparseArray.keyAt(i));
                }
            }
            TransitionManager.beginDelayedTransition(config.parent, config.transition);
        }
        ViewInfoData parentData = sparseArray.get(config.parent.getId());
        if (parentData != null) {
            parentData.customProperties.applyAll(config.parent);
        }
        applyToViews(config, config.parent, sparseArray);
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

    private static boolean shouldGenerateChild(View view) {
        return view instanceof ViewGroup;
    }

    private static class ViewInfoData {
        private ViewGroup.LayoutParams layoutParams;
        private CustomProperties customProperties = new CustomProperties();
        private boolean hasChild;
    }
}
