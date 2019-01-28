package com.example.gdcrry.layoutadapterdemo.layout_adapter;

import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * Created by zhangtianye.bugfree on 2019/1/17.
 */
public class XmlLayoutAdapter {

    private static volatile XmlLayoutAdapter instance;

    private static XmlLayoutAdapter get() {
        if (instance == null) {
            synchronized (XmlLayoutAdapter.class) {
                if (instance == null) {
                    instance = new XmlLayoutAdapter();
                }
            }
        }
        return instance;
    }

    private Executor asyncExecutor = Runnable::run;
    private ErrorHandler errorHandler = Throwable::printStackTrace;

    private XmlLayoutAdapter() {
    }

    public static void config(@NonNull Executor executor, @Nullable ErrorHandler errorHandler) {
        get().asyncExecutor = executor;
        get().errorHandler = errorHandler;
    }

    public static void release() {
        get().asyncExecutor = null;
        get().errorHandler = null;
        XmlLayoutAdapterImpl.release();
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
        Set<Integer> ignoreChildIdSet = new HashSet<>();

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

        /**
         * 使用 AutoTransition 并设置时长
         *
         * @param duration 单位 ms
         */
        public Config animate(long duration) {
            this.animate = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                this.transition = new AutoTransition()
                        .setDuration(duration)
                        .setOrdering(TransitionSet.ORDERING_TOGETHER); // 如果顺序执行可能会有闪烁的问题
            }
            return this;
        }

        /**
         * 不会对 id 为指定值的 ViewGroup 中的子 View 做处理
         */
        public Config ignoreChild(@IdRes int id) {
            ignoreChildIdSet.add(id);
            return this;
        }

        public void execute() {
            execute(false);
        }

        public void execute(boolean async) {
            execute(async, null);
        }

        /**
         * 执行, 可以指定异步. 异步时最好确保执行后其他代码不会再(在此帧中)
         * 改变布局, 否则其他代码造成的变化有可能稍提前于此工具的变化, 造成
         * 界面闪烁.
         *
         * @param async    是否异步
         * @param callback 在所有属性均应用到 View 后, 在主线程上回调
         *                 注意此时动画可能还未结束 (一般是刚刚开始)
         */
        public void execute(boolean async, Runnable callback) {
            this.async = async;
            this.callback = callback;
            XmlLayoutAdapterImpl.getInstance().apply(this);
        }
    }

    public interface ErrorHandler {
        void onException(Exception e);
    }
}
