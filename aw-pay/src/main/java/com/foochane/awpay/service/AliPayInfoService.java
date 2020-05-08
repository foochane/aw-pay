package com.foochane.awpay.service;

import com.foochane.awpay.dao.entity.AliPayInfo;


public interface AliPayInfoService {

    AliPayInfo getByPayChannel(String payChannel);
}
