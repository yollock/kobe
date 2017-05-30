package com.yollock.kobe.serialize;

import com.yollock.kobe.common.extension.Extension;

import java.io.IOException;

@Extension("fastjson")
public interface Serialization {

    byte[] serialize(Object obj) throws IOException;

    <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException;

}
