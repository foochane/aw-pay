package com.foochane.awpay.controller;

import com.alipay.api.response.*;
import com.foochane.awpay.common.request.*;
import com.foochane.awpay.common.result.*;
import com.foochane.awpay.common.utils.IdWorker;
import com.foochane.awpay.service.AliPayService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
public class AlipayController {


    @Resource
    private AliPayService aliPayService;


    @ResponseBody
    @RequestMapping(value = "/ali/pay/order/create",method = RequestMethod.POST)
    public Result<AlipayTradePagePayResponse>  create(@RequestBody OrderCreateRequest request)  {

        if(StringUtils.isEmpty(request.getPayChannel())){
            return Result.error(-1,"payChannel不能为空");
        }

        if(StringUtils.isEmpty(request.getProductId())){
            return Result.error(-1,"productId不能为空");
        }

        if(StringUtils.isEmpty(request.getSubject())){
            return Result.error(-1,"subject不能为空");
        }

        if(StringUtils.isEmpty(request.getBody())){
            return Result.error(-1,"body不能为空");
        }

        if(StringUtils.isEmpty(request.getPayAmount())){
            return Result.error(-1,"payAmount不能为空");
        }

        return aliPayService.create(request);
    }


    @ResponseBody
    @RequestMapping(value = "/ali/pay/order/query",method = RequestMethod.POST)
    public Result<AlipayTradeQueryResponse> query(@RequestBody OrderQueryRequest request)  {
        if(StringUtils.isEmpty(request.getOutTradeNo())){
            return Result.error(-1,"outTradeNo不能为空");
        }

        return aliPayService.query(request);
    }

    @ResponseBody
    @RequestMapping(value = "/ali/pay/order/close",method = RequestMethod.POST)
    public Result<AlipayTradeCloseResponse> close(@RequestBody OrderCloseRequest request)  {

        if(StringUtils.isEmpty(request.getOutTradeNo())){
            return Result.error(-1,"outTradeNo不能为空");
        }

        return aliPayService.close(request);
    }

    @ResponseBody
    @RequestMapping(value = "/api/pay/order/refund",method = RequestMethod.POST)
    public Result<AlipayTradeRefundResponse> query(@RequestBody OrderRefundRequest request)  {

        if(StringUtils.isEmpty(request.getOutTradeNo())){
            return Result.error(-1,"outTradeNo不能为空");
        }

        if(StringUtils.isEmpty(request.getPayAmount())){
            return Result.error(-1,"payAmount不能为空");
        }

        if(StringUtils.isEmpty(request.getRefundAmount())){
            return Result.error(-1,"refundAmount不能为空");
        }

        if(StringUtils.isEmpty(request.getRefundReason())){
            return Result.error(-1,"refundReason不能为空");
        }

        String outRefundNo = "R" + IdWorker.getId();
        request.setOutRefundNo(outRefundNo);

        return aliPayService.refund(request);
    }

    @ResponseBody
    @RequestMapping(value = "/ali/pay/refund/query",method = RequestMethod.POST)
    public Result<AlipayTradeFastpayRefundQueryResponse> query(@RequestBody RefundQueryRequest request)  {

        if(StringUtils.isEmpty(request.getOutTradeNo())){
            return Result.error(-1,"outTradeNo不能为空");
        }

        return aliPayService.refundQuery(request);
    }

}
