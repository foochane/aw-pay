package com.foochane.awpay.controller;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.foochane.awpay.common.request.*;
import com.foochane.awpay.common.result.*;
import com.foochane.awpay.common.utils.IdWorker;
import com.foochane.awpay.service.AliPayService;
import com.foochane.awpay.service.WeiXinPayService;
import com.github.binarywang.wxpay.constant.WxPayErrorCode;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;


@RestController
public class AwPayController {


    @Resource
    private AliPayService aliPayService;

    @Resource
    private WeiXinPayService weiXinPayService;

    @ResponseBody
    @RequestMapping(value = "/api/pay/order/create",method = RequestMethod.POST)
    public Result<OrderCreateResult> create(@RequestBody OrderCreateRequest request)  {

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

        String payChannel = request.getPayChannel();

        switch (payChannel) {
            case "ALIPAY_PC":
            case "ALIPAY_WAP":
                return aliPayService.create(request);
            case "WXPAY_NATIVE":
            case "WXPAY_WAP":
                return weiXinPayService.create(request);
            default:
                return Result.error(-1,"不支持此支付方式");
        }
    }


    @ResponseBody
    @RequestMapping(value = "/api/pay/order/query",method = RequestMethod.POST)
    public Result<QueryCreateResult> query(@RequestBody OrderQueryRequest request)  {

        if(StringUtils.isEmpty(request.getPayChannel())){
            return Result.error(-1,"payChannel不能为空");
        }

        if(StringUtils.isEmpty(request.getOutTradeNo())){
            return Result.error(-1,"outTradeNo不能为空");
        }

        switch (request.getPayChannel()) {
            case "ALIPAY_PC":
            case "ALIPAY_WAP":
                return aliPayService.query(request);
            case "WXPAY_NATIVE":
            case "WXPAY_WAP":
                return weiXinPayService.query(request);
            default:
                return Result.error(-1,"不支持此支付方式");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/api/pay/order/close",method = RequestMethod.POST)
    public Result<OrderCloseResult> close(@RequestBody OrderCloseRequest request)  {

        if(StringUtils.isEmpty(request.getPayChannel())){
            return Result.error(-1,"payChannel不能为空");
        }

        if(StringUtils.isEmpty(request.getOutTradeNo())){
            return Result.error(-1,"outTradeNo不能为空");
        }

        switch (request.getPayChannel()) {
            case "ALIPAY_PC":
            case "ALIPAY_WAP":
                return aliPayService.close(request);
            case "WXPAY_NATIVE":
            case "WXPAY_WAP":
                return weiXinPayService.close(request);
            default:
                return Result.error(-1,"不支持此支付方式");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/api/pay/order/refund",method = RequestMethod.POST)
    public Result<OrderRefundResult> query(@RequestBody OrderRefundRequest request)  {
        if(StringUtils.isEmpty(request.getPayChannel())){
            return Result.error(-1,"payChannel不能为空");
        }

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

        switch (request.getPayChannel()) {
            case "ALIPAY_PC":
            case "ALIPAY_WAP":
                return aliPayService.refund(request);
            case "WXPAY_NATIVE":
            case "WXPAY_WAP":
                return weiXinPayService.refund(request);
            default:
                return Result.error(-1,"不支持此支付方式");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/api/pay/refund/query",method = RequestMethod.POST)
    public Result<RefundQueryResult> query(@RequestBody RefundQueryRequest request)  {
        if(StringUtils.isEmpty(request.getPayChannel())){
            return Result.error(-1,"payChannel不能为空");
        }

        if(StringUtils.isEmpty(request.getOutTradeNo())){
            return Result.error(-1,"outTradeNo不能为空");
        }

        switch (request.getPayChannel()) {
            case "ALIPAY_PC":
            case "ALIPAY_WAP":
                return aliPayService.refundQuery(request);
            case "WXPAY_NATIVE":
            case "WXPAY_WAP":
                return weiXinPayService.refundQuery(request);
            default:
                return Result.error(-1,"不支持此支付方式");
        }
    }

}
