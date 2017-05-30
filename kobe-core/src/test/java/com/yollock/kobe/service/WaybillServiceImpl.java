package com.yollock.kobe.service;

public class WaybillServiceImpl implements WaybillService {
    @Override
    public Waybill getById(String id) {
        return new Waybill(100L, "WW123123123");
    }
}
