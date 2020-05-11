package com.foochane.awpay.controller;

import com.foochane.awpay.common.request.OrderCloseRequest;
import com.foochane.awpay.common.result.Result;
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

    @RequestMapping(value = "/error1")
    @ResponseBody
    public Result<Integer> error1(){
        return Result.error();
    }

    @RequestMapping(value = "/error2")
    @ResponseBody
    public Result<Integer> error2(){
        return Result.error(-1,"错误");
    }

    @RequestMapping(value = "/error3")
    @ResponseBody
    public Result<OrderCloseRequest> error3(){
        OrderCloseRequest orderCloseRequest = new OrderCloseRequest();
        orderCloseRequest.setOutTradeNo("xxxx");
        return Result.error(-1,"错误",orderCloseRequest);
    }

    @RequestMapping(value = "/success1")
    @ResponseBody
    public Result success1(){
        return Result.success();
    }

    @RequestMapping(value = "/success2")
    @ResponseBody
    public Result success2(){
        return Result.error(-1,"错误");
    }

    @RequestMapping(value = "/success3")
    @ResponseBody
    public Result<OrderCloseRequest> success3(){
        OrderCloseRequest orderCloseRequest = new OrderCloseRequest();
        orderCloseRequest.setOutTradeNo("xxxx");
        return Result.error(-1,"错误",orderCloseRequest);
    }


}
