package com.foochane.awpay.controller;

import com.foochane.awpay.common.request.*;
import com.foochane.awpay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PayController {

    @Autowired
    private PayService payService;

    @ResponseBody
    @RequestMapping(value = "/api/pay/order/create",method = RequestMethod.POST)
    public String create(@RequestBody OrderCreateRequest request)  {
        return payService.create(request);
    }

    @ResponseBody
    @RequestMapping(value = "/api/pay/order/query",method = RequestMethod.POST)
    public String query(@RequestBody OrderQueryRequest request)  {
        return payService.query(request);
    }

    @ResponseBody
    @RequestMapping(value = "/api/pay/order/close",method = RequestMethod.POST)
    public String query(@RequestBody OrderCloseRequest request)  {
        return payService.close(request);
    }

    @ResponseBody
    @RequestMapping(value = "/api/pay/order/refund",method = RequestMethod.POST)
    public String query(@RequestBody OrderRefundRequest request)  {
        return payService.refund(request);
    }

    @ResponseBody
    @RequestMapping(value = "/api/pay/refund/query",method = RequestMethod.POST)
    public String query(@RequestBody RefundQueryRequest request)  {
        return payService.refundQuery(request);
    }
}
