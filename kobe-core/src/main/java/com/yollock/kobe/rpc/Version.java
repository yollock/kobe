package com.yollock.kobe.rpc;

public enum Version {

    /**
     * 版本规则:版本号分为3部分,以0为分隔符; 第1部分,表示主版本, 第2部分,表示次版本, 第3部分,表示修复版本
     */
    CURRENT(10101);

    Version(int version) {
        this.version = version;
    }

    private int version;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
