package com.foochane.awpay.dao.entity;

import java.util.Date;

public class WxPayInfo {
    private Integer id;

    private String payChannel;

    private String appId;

    private String mchId;

    private String mahKey;

    private String certLocalPath;

    private String remark;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModified;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel == null ? null : payChannel.trim();
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId == null ? null : appId.trim();
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId == null ? null : mchId.trim();
    }

    public String getMahKey() {
        return mahKey;
    }

    public void setMahKey(String mahKey) {
        this.mahKey = mahKey == null ? null : mahKey.trim();
    }

    public String getCertLocalPath() {
        return certLocalPath;
    }

    public void setCertLocalPath(String certLocalPath) {
        this.certLocalPath = certLocalPath == null ? null : certLocalPath.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
}