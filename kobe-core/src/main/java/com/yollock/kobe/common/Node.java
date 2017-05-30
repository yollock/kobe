package com.yollock.kobe.common;

public interface Node {

    void init() throws InterruptedException;

    void close();

    boolean isAvailable();

    URL getUrl();

    String desc();
}


