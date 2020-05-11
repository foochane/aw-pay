package com.foochane.awpay.common.request;

import java.io.Serializable;

public class OrderCloseRequest implements Serializable {



    private String outTradeNo;


    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }
}
