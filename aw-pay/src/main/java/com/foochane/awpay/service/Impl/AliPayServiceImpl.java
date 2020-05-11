package com.foochane.awpay.service.Impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.foochane.awpay.common.request.*;
import com.foochane.awpay.common.result.*;
import com.foochane.awpay.common.utils.IdWorker;
import com.foochane.awpay.dao.entity.*;
import com.foochane.awpay.dao.mapper.AliPayInfoMapper;
import com.foochane.awpay.dao.mapper.PayOrderMapper;
import com.foochane.awpay.service.AliPayService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;


@Service
public class AliPayServiceImpl implements AliPayService {

    @Resource
    private AliPayInfoMapper aliPayInfoMapper;


    @Resource
    private PayOrderMapper payOrderMapper;

    @Override
    public Result<AlipayTradePagePayResponse> create(OrderCreateRequest request) {
        String payChannel = request.getPayChannel();
        PayOrder payOrder = new PayOrder();
        payOrder.setOutTradeNo("P" + IdWorker.getId());
        payOrder.setProductId(request.getProductId());
        payOrder.setSubject(request.getSubject());
        payOrder.setBody(request.getBody());
        payOrder.setPayAmount(request.getPayAmount());
        payOrder.setPayChannel(payChannel);
        payOrder.setStatus((byte) 0); // 支付状态,0-生成支付订单,1-支付成功,2-退款成功，3-订单关闭，4-业务处理完成

        String form;
        switch (payChannel) {
            case "ALIPAY_PC":
                return Result.success(1, "订单生成", aliPayOrderCreate(payOrder));

            case "ALIPAY_WAP":
                return null;
            default:
                return Result.error(-1, "不支持此支付方式");
        }
    }


    @Override
    public Result<AlipayTradeQueryResponse> query(OrderQueryRequest request) {

        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria c = example.createCriteria();
        c.andOutTradeNoEqualTo(request.getOutTradeNo());
        List<PayOrder> list = payOrderMapper.selectByExample(example);
        if (list.isEmpty()) {
            return Result.error(-1, "没有查到订单信息");
        }

        PayOrder payOrder = list.get(0);


        System.out.println("订单查询...");

        AliPayInfo aliPayInfo = getAliPayInfo(payOrder.getPayChannel());

        if(ObjectUtils.isEmpty(aliPayInfo)){
            return Result.error(-1,"支付渠道错误,该订单支付渠道为：" + payOrder.getPayChannel());
        }

        // 创建AlipayClient
        AlipayClient alipayClient = initAlipayClient(aliPayInfo.getIsSandbox(),
                aliPayInfo.getAppId(),
                aliPayInfo.getMerchantPrivateKey(),
                aliPayInfo.getAlipayPublicKey());

        //设置请求参数
        AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();

        // out_trade_no 和 trade_no 设置二选一即可
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + payOrder.getOutTradeNo() + "\"," + "\"trade_no\":\"" + "" + "\"}");

        //请求
        String result = null;
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(alipayRequest);
            return Result.success(1, "订单查询", response);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return Result.error(-1, "操作失败");
    }


    @Override
    public Result<AlipayTradeCloseResponse> close(OrderCloseRequest request) {
        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria c = example.createCriteria();
        c.andOutTradeNoEqualTo(request.getOutTradeNo());
        List<PayOrder> list = payOrderMapper.selectByExample(example);
        if (list.isEmpty()) {
            return Result.error(-1, "没有查到订单信息");
        }

        PayOrder payOrder = list.get(0);


        System.out.println("订单关闭...");


        AliPayInfo aliPayInfo = getAliPayInfo(payOrder.getPayChannel());
        if(ObjectUtils.isEmpty(aliPayInfo)){
            return Result.error(-1,"支付渠道错误,该订单支付渠道为：" + payOrder.getPayChannel());
        }

        // 创建AlipayClient
        AlipayClient alipayClient = initAlipayClient(aliPayInfo.getIsSandbox(),
                aliPayInfo.getAppId(),
                aliPayInfo.getMerchantPrivateKey(),
                aliPayInfo.getAlipayPublicKey());

        //设置请求参数
        AlipayTradeCloseRequest alipayRequest = new AlipayTradeCloseRequest();

        // out_trade_no 和 trade_no 设置二选一即可
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + payOrder.getOutTradeNo() + "\"," + "\"trade_no\":\"" + "" + "\"}");

        //请求
        String result = null;
        try {
            AlipayTradeCloseResponse response = alipayClient.execute(alipayRequest);
            return Result.success(1, "订单关闭", response);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return Result.error(-1, "操作失败");
    }

    @Override
    public Result<AlipayTradeRefundResponse> refund(OrderRefundRequest request) {
        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria c = example.createCriteria();
        c.andOutTradeNoEqualTo(request.getOutTradeNo());
        List<PayOrder> list = payOrderMapper.selectByExample(example);
        if (list.isEmpty()) {
            return Result.error(-1, "没有查到订单信息");
        }

        PayOrder payOrder = list.get(0);


        System.out.println("退款...");

        AliPayInfo aliPayInfo = getAliPayInfo(payOrder.getPayChannel());

        if(ObjectUtils.isEmpty(aliPayInfo)){
            return Result.error(-1,"支付渠道错误,该订单支付渠道为：" + payOrder.getPayChannel());
        }

        // 创建AlipayClient
        AlipayClient alipayClient = initAlipayClient(aliPayInfo.getIsSandbox(),
                aliPayInfo.getAppId(),
                aliPayInfo.getMerchantPrivateKey(),
                aliPayInfo.getAlipayPublicKey());

        //设置请求参数
        AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();


        alipayRequest.setBizContent("{\"out_trade_no\":\"" + request.getOutTradeNo() + "\","
                + "\"trade_no\":\"" + "" + "\","
                + "\"refund_amount\":\"" + payOrder.getPayAmount() / 100.0 + "\","
                + "\"refund_reason\":\"" + request.getRefundReason() + "\","
                + "\"out_request_no\":\"" + "" + "\"}");

        //请求
        String result = null;
        try {
            AlipayTradeRefundResponse response = alipayClient.execute(alipayRequest);
            return Result.success(1, "退款", response);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return Result.error(-1, "操作失败");

    }

    @Override
    public Result<AlipayTradeFastpayRefundQueryResponse> refundQuery(RefundQueryRequest request) {
        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria c = example.createCriteria();
        c.andOutTradeNoEqualTo(request.getOutTradeNo());
        List<PayOrder> list = payOrderMapper.selectByExample(example);
        if (list.isEmpty()) {
            return Result.error(-1, "没有查到订单信息");
        }

        PayOrder payOrder = list.get(0);


        System.out.println("退款查询...");


        AliPayInfo aliPayInfo = getAliPayInfo(payOrder.getPayChannel());

        if(ObjectUtils.isEmpty(aliPayInfo)){
            return Result.error(-1,"支付渠道错误,该订单支付渠道为：" + payOrder.getPayChannel());
        }

        // 创建AlipayClient
        AlipayClient alipayClient = initAlipayClient(aliPayInfo.getIsSandbox(),
                aliPayInfo.getAppId(),
                aliPayInfo.getMerchantPrivateKey(),
                aliPayInfo.getAlipayPublicKey());

        //设置请求参数
        AlipayTradeFastpayRefundQueryRequest alipayRequest = new AlipayTradeFastpayRefundQueryRequest();
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + request.getOutTradeNo() + "\","
                + "\"trade_no\":\"" + "" + "\","
                + "\"out_request_no\":\"" + "" + "\"}");

        //请求
        String result = null;
        try {
            AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(alipayRequest);
            return Result.success(1, "退款查询", response);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return Result.error(-1, "操作失败");

    }


    private AlipayTradePagePayResponse aliPayOrderCreate(PayOrder payOrder) {


        AliPayInfo aliPayInfo = getAliPayInfo(payOrder.getPayChannel());


        // 创建AlipayClient
        AlipayClient alipayClient = initAlipayClient(aliPayInfo.getIsSandbox(),
                aliPayInfo.getAppId(),
                aliPayInfo.getMerchantPrivateKey(),
                aliPayInfo.getAlipayPublicKey());

        // 设置请求参数
        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = payOrder.getOutTradeNo();
        //付款金额，必填
        String total_amount = String.valueOf(payOrder.getPayAmount() / 100.0);  // 将分转成元
        //订单名称，必填
        String subject = payOrder.getSubject();
        //商品描述，可空
        String body = payOrder.getBody();
        // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
        String timeout_express = "10m";
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest(); //创建API对应的request
        alipayRequest.setReturnUrl(aliPayInfo.getReturnUrl());
        alipayRequest.setNotifyUrl(aliPayInfo.getNotifyUrl()); //在公共参数中设置回跳和通知地址
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"timeout_express\":\"" + timeout_express + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}"); //填充业务参数

        // 3 发起支付请求
        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(alipayRequest);
            response.setBody(response.getBody().replace("\n","").replace("\"","'"));

            savePayOrder(payOrder);

            return response;

        } catch (AlipayApiException e) {
            e.printStackTrace();
        }


        return null;
    }


    private AliPayInfo getAliPayInfo(String payChannel) {

        AliPayInfoExample example = new AliPayInfoExample();
        AliPayInfoExample.Criteria c = example.createCriteria();
        c.andPayChannelEqualTo(payChannel);
        List<AliPayInfo> list = aliPayInfoMapper.selectByExample(example);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    private AlipayClient initAlipayClient(Byte isSandbox, String appId, String merchantPrivateKey, String alipayPublicKey) {
        String gatewayUrl = "";
        if (isSandbox == 1) {
            // 沙箱测试环境
            gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
        } else {
            // 正式环境
            gatewayUrl = "https://openapi.alipay.com/gateway.do";
        }


        // 创建AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                appId,
                merchantPrivateKey,
                "json",
                "utf-8",
                alipayPublicKey,
                "RSA2");
        return alipayClient;
    }


    private void savePayOrder(PayOrder payOrder) {

        payOrderMapper.insertSelective(payOrder);
    }


}
