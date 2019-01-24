package com.example.gdcrry.layoutadapterdemo.layout_adapter;

import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.Transition;
import android.view.ViewGroup;

import java.util.concurrent.Executor;

/**
 * Created by zhangtianye.bugfree on 2019/1/17.
 */
public class LayoutAdapter {

    private static volatile LayoutAdapter instance;

    private static LayoutAdapter get() {
        if (instance == null) {
            synchronized (LayoutAdapter.class) {
                if (instance == null) {
                    instance = new LayoutAdapter();
                }
            }
        }
        return instance;
    }

    private Executor asyncExecutor = Runnable::run;
    private ErrorHandler errorHandler = Throwable::printStackTrace;

    private LayoutAdapter() {
    }

    public static void config(@NonNull Executor executor, @Nullable ErrorHandler errorHandler) {
        get().asyncExecutor = executor;
        get().errorHandler = errorHandler;
    }

    public static void release() {
        get().asyncExecutor = null;
        get().errorHandler = null;
        LayoutAdapterImpl.release();
        instance = null;
    }

    public static Config of(Resources resources) {
        return new Config(resources, get().asyncExecutor, get().errorHandler);
    }

    public static class Config {
        Resources resources;
        Executor asyncExecutor;
        ErrorHandler errorHandler;
        ViewGroup parent;
        Transition transition;
        int layoutId;
        boolean async = false;
        Runnable callback;
        boolean animate = false;

        private Config(Resources resources, Executor asyncExecutor, ErrorHandler errorHandler) {
            this.resources = resources;
            this.asyncExecutor = asyncExecutor;
            this.errorHandler = errorHandler;
        }

        public Config apply(@LayoutRes int layoutId) {
            this.layoutId = layoutId;
            return this;
        }

        public Config to(ViewGroup parent) {
            this.parent = parent;
            return this;
        }

        public Config animate() {
            this.animate = true;
            return this;
        }

        public Config animate(Transition transition) {
            this.animate = true;
            this.transition = transition;
            return this;
        }

        public void execute() {
            execute(false);
        }

        public void execute(boolean async) {
            execute(async, null);
        }

        public void execute(boolean async, Runnable callback) {
            this.async = async;
            this.callback = callback;
            LayoutAdapterImpl.getInstance().apply(this);
        }
    }

    public interface ErrorHandler {
        void onException(Exception e);
    }
}
