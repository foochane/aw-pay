package com.foochane.awpay.controller;

import com.foochane.awpay.dao.entity.AliPayInfo;
import com.foochane.awpay.service.AliPayInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    @Autowired
    private AliPayInfoService aliPayInfoService;


    @RequestMapping(value = "/ali/pay/info/get")
    @ResponseBody
    public AliPayInfo getByPayChannel(String payChannel){
        return aliPayInfoService.getByPayChannel(payChannel);
    }
}
