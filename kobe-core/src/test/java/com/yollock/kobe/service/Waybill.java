package com.yollock.kobe.service;


public class Waybill {

    private Long id;

    private String code;

    public Waybill() {
        super();
    }

    public Waybill(Long id, String code) {
        super();
        this.id = id;
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
