package com.yollock.kobe.transport;

import com.yollock.kobe.common.exception.KobeTransportException;
import com.yollock.kobe.rpc.*;

/**
 * Created by yollock on 2016/12/28.
 */
public interface Channel extends Endpoint {

    Response request(Request request) throws KobeTransportException;

    Response request(Request request, int timeout) throws KobeTransportException;
}
