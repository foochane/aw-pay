package com.foochane.awpay.service.Impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.foochane.awpay.common.config.MyAliPayConfig;
import com.foochane.awpay.common.request.*;
import com.foochane.awpay.common.utils.IdWorker;
import com.foochane.awpay.dao.entity.*;
import com.foochane.awpay.dao.mapper.AliPayInfoMapper;
import com.foochane.awpay.dao.mapper.PayOrderMapper;
import com.foochane.awpay.dao.mapper.WxPayInfoMapper;
import com.foochane.awpay.service.PayService;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private AliPayInfoMapper aliPayInfoMapper;

    @Autowired
    private WxPayInfoMapper wxPayInfoMapper;

    @Autowired
    private PayOrderMapper payOrderMapper;

    @Override
    public String create(OrderCreateRequest request) {
        String payChannel = request.getPayChannel();
        PayOrder payOrder = new PayOrder();
        payOrder.setOutTradeNo("P" + IdWorker.getId());
        payOrder.setProductId(request.getProductId());
        payOrder.setSubject(request.getSubject());
        payOrder.setBody(request.getBody());
        payOrder.setPayAmount(request.getPayAmount());
        payOrder.setPayChannel(payChannel);
        payOrder.setStatus((byte) 0); // 支付状态,0-订单生成,1-支付中,2-支付成功,3-退款成功，4-订单关闭，5-业务处理完成
        switch (payChannel) {
            case "ALIPAY_PC":
                return aliPayOrderCreate(payOrder);
//            case "ALIPAY_WAP":
//                return aliPayOrderCreate();
            case "WXPAY_NATIVE":
                return wxPayOrderCreate(payOrder);
//            case "WXPAY_WAP":
//                return wxPayOrderCreate();
            default:
                return "不支持此支付方式";
        }
    }



    @Override
    public String query(OrderQueryRequest request) {

        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria c = example.createCriteria();
        c.andOutTradeNoEqualTo(request.getOutTradeNo());
        List<PayOrder> list = payOrderMapper.selectByExample(example);
        if (list.isEmpty()) {
            return "没有查到订单信息";
        }

        String result = "";
        PayOrder payOrder = list.get(0);

        String payChannel = payOrder.getPayChannel();

        switch (payChannel){
            case "ALIPAY_PC":
            case "ALIPAY_WAP":
                result = aliPayQuery(payOrder);
                break;
            case "WXPAY_NATIVE":
            case "WXPAY_WAP":
                result = wxPayQuery(payOrder);
                break;
            default:
                result = "error";
        }
        return result;
    }




    @Override
    public String close(OrderCloseRequest request) {
        return null;
    }

    @Override
    public String refund(OrderRefundRequest request) {
        return null;
    }

    @Override
    public String refundQuery(RefundQueryRequest request) {
        return null;
    }

    private String aliPayOrderCreate(PayOrder payOrder) {


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
        String total_amount = String.valueOf(payOrder.getPayAmount()/100.0);  // 将分转成元
        //订单名称，必填
        String subject = payOrder.getSubject();
        //商品描述，可空
        String body = payOrder.getBody();
        // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
        String timeout_express = "10m";
        AlipayTradePagePayRequest alipayRequest =  new  AlipayTradePagePayRequest(); //创建API对应的request
        alipayRequest.setReturnUrl(aliPayInfo.getReturnUrl());
        alipayRequest.setNotifyUrl(aliPayInfo.getNotifyUrl()); //在公共参数中设置回跳和通知地址
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"timeout_express\":\"" + timeout_express + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}"); //填充业务参数

        // 3 发起支付请求
        String form = "";
        try  {
            form = alipayClient.pageExecute(alipayRequest).getBody();  //调用SDK生成表单
        }  catch  (AlipayApiException e) {
            e.printStackTrace();
        }

        System.out.println("*************************** 支付请求成功 *******************************");
        System.out.println("返回表单：");
        System.out.println(form);
        System.out.println("*************************** 支付请求成功 *******************************");

        savePayOrder(payOrder);

        return form;
    }




    private String wxPayOrderCreate(PayOrder payOrder) {

        WxPayInfo wxPayInfo = getWxPayInfo(payOrder.getPayChannel());


        WxPayService wxPayService = initWxPayService(wxPayInfo.getAppId(),wxPayInfo.getMchId(),wxPayInfo.getMahKey(),wxPayInfo.getCertLocalPath());

        WxPayUnifiedOrderRequest wxPayUnifiedOrderRequest = new WxPayUnifiedOrderRequest();
        wxPayUnifiedOrderRequest.setTradeType("NATIVE");
        wxPayUnifiedOrderRequest.setOutTradeNo(payOrder.getOutTradeNo());
        wxPayUnifiedOrderRequest.setBody(payOrder.getBody());
        wxPayUnifiedOrderRequest.setProductId(payOrder.getProductId());
        wxPayUnifiedOrderRequest.setTotalFee(Math.toIntExact(payOrder.getPayAmount()));
        wxPayUnifiedOrderRequest.setSpbillCreateIp("1.1.1.1");
        wxPayUnifiedOrderRequest.setNotifyUrl("xxxx");  //TODO 放数据库


        String result = "";
        try {
            result = wxPayService.unifiedOrder(wxPayUnifiedOrderRequest).toString();
//            result = wxPayService.createOrder(wxPayUnifiedOrderRequest).toString();
            savePayOrder(payOrder);
            return result;
        } catch (WxPayException e) {
            e.printStackTrace();
        }

        return result;
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
        if(isSandbox == 1){
            // 沙箱测试环境
            gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
        }else {
            // 正式环境
            gatewayUrl = "https://openapi.alipay.com/gateway.do";
        }


        // 创建AlipayClient
        AlipayClient alipayClient =  new DefaultAlipayClient(gatewayUrl,
                appId,
                merchantPrivateKey,
                "json",
                "utf-8",
                alipayPublicKey,
                "RSA2");
        return alipayClient;
    }

    private WxPayInfo getWxPayInfo(String payChannel) {

        WxPayInfoExample example = new WxPayInfoExample();
        WxPayInfoExample.Criteria c = example.createCriteria();
        c.andPayChannelEqualTo(payChannel);
        List<WxPayInfo> list = wxPayInfoMapper.selectByExample(example);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    private WxPayService initWxPayService(String appId, String mchId, String mahKey, String certLocalPath) {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(appId);
        payConfig.setMchId(mchId);
        payConfig.setMchKey(mahKey);
        payConfig.setKeyPath(certLocalPath);
        payConfig.setUseSandboxEnv(false); //不使用沙箱环境

        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return  wxPayService;
    }

    private void savePayOrder(PayOrder payOrder) {

        payOrderMapper.insertSelective(payOrder);
    }

    private String aliPayQuery(PayOrder payOrder) {
        System.out.println("订单查询...");

        AliPayInfo aliPayInfo = getAliPayInfo(payOrder.getPayChannel());



        // 创建AlipayClient
        AlipayClient alipayClient = initAlipayClient(aliPayInfo.getIsSandbox(),
                aliPayInfo.getAppId(),
                aliPayInfo.getMerchantPrivateKey(),
                aliPayInfo.getAlipayPublicKey());

        //设置请求参数
        AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();

        // out_trade_no 和 trade_no 设置二选一即可
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ payOrder.getOutTradeNo() +"\","+"\"trade_no\":\""+ "" +"\"}");

        //请求
        String result = null;
        try {
            result = alipayClient.execute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        //输出
        System.out.println("订单查询结果：" +result);

        return  result;
    }

    private String wxPayQuery(PayOrder payOrder) {
        WxPayInfo wxPayInfo = getWxPayInfo(payOrder.getPayChannel());


        WxPayService wxPayService = initWxPayService(wxPayInfo.getAppId(),wxPayInfo.getMchId(),wxPayInfo.getMahKey(),wxPayInfo.getCertLocalPath());
        String result= "";
        try {
            result = wxPayService.queryOrder("", payOrder.getOutTradeNo()).toString();
        } catch (WxPayException e) {
            e.printStackTrace();
        }
        return result;
    }
}
