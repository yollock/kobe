package com.yollock.kobe.common;

import com.yollock.kobe.common.exception.KobeFrameException;
import com.yollock.kobe.util.LoggerUtil;
import com.yollock.kobe.util.ReflectUtil;

public abstract class AbstractNode implements Node {

    protected URL url;

    protected volatile boolean init = false;
    protected volatile boolean available = false;

    public AbstractNode(URL url) {
        this.url = url;
    }

    @Override
    public synchronized void init() throws InterruptedException {
        if (init) {
            LoggerUtil.warn(ReflectUtil.simpleClassName(this) + " node already init: " + desc());
            return;
        }

        boolean result = doInit();

        if (!result) {
            LoggerUtil.error(ReflectUtil.simpleClassName(this) + " node init Error: " + desc());
            throw new KobeFrameException(ReflectUtil.simpleClassName(this) + " node init Error: " + desc());
        } else {
            LoggerUtil.info(ReflectUtil.simpleClassName(this) + " node init Success: " + desc());
            init = true;
            available = true;
        }
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public URL getUrl() {
        return url;
    }

    protected abstract boolean doInit() throws InterruptedException;
}
