package com.foochane.awpay.common.request;

import java.io.Serializable;

public class OrderCloseRequest implements Serializable {


    private String payChannel;

    private String outTradeNo;

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }
}
