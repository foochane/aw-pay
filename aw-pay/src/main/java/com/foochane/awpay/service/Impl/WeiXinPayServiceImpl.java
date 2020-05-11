package com.foochane.awpay.service.Impl;

import com.foochane.awpay.common.request.*;
import com.foochane.awpay.common.result.*;
import com.foochane.awpay.common.utils.IdWorker;
import com.foochane.awpay.dao.entity.*;
import com.foochane.awpay.dao.mapper.PayOrderMapper;
import com.foochane.awpay.dao.mapper.WxPayInfoMapper;
import com.foochane.awpay.service.WeiXinPayService;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class WeiXinPayServiceImpl implements WeiXinPayService {


    @Resource
    private WxPayInfoMapper wxPayInfoMapper;

    @Resource
    private PayOrderMapper payOrderMapper;

    @Override
    public Result<OrderCreateResult> create(OrderCreateRequest request) {
        String payChannel = request.getPayChannel();
        PayOrder payOrder = new PayOrder();
        payOrder.setOutTradeNo("P" + IdWorker.getId());
        payOrder.setProductId(request.getProductId());
        payOrder.setSubject(request.getSubject());
        payOrder.setBody(request.getBody());
        payOrder.setPayAmount(request.getPayAmount());
        payOrder.setPayChannel(payChannel);
        payOrder.setStatus((byte) 0); // 支付状态,0-生成支付订单,1-支付成功,2-退款成功，3-订单关闭，4-业务处理完成
        String codeURL = "";
        switch (payChannel) {
            case "WXPAY_NATIVE":
                codeURL =  wxPayOrderCreate(payOrder);
                break;
            case "WXPAY_WAP":
                codeURL = "暂不支持";
                break;
            default:
                codeURL = "";
        }

        OrderCreateResult orderCreateResult = new OrderCreateResult();
        orderCreateResult.setOutTradeNo(payOrder.getOutTradeNo());
        orderCreateResult.setPayChannel(payOrder.getPayChannel());
        orderCreateResult.setReturnMsg("订单生成");
        orderCreateResult.setPayInfo(codeURL);
        return Result.success(1,"SUCCESS",orderCreateResult);
    }



    @Override
    public Result<QueryCreateResult> query(OrderQueryRequest request) {

        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria c = example.createCriteria();
        c.andOutTradeNoEqualTo(request.getOutTradeNo());
        List<PayOrder> list = payOrderMapper.selectByExample(example);
        if (list.isEmpty()) {
            return Result.error(-1,"没有查到订单信息") ;
        }

        PayOrder payOrder = list.get(0);

        // 核对支付渠道
        if(!payOrder.getPayChannel().equals(request.getPayChannel())){
            return Result.error(-1,"payChannel错误") ;
        }

        WxPayInfo wxPayInfo = getWxPayInfo(payOrder.getPayChannel());


        WxPayService wxPayService = initWxPayService(wxPayInfo.getAppId(),wxPayInfo.getMchId(),wxPayInfo.getMahKey(),wxPayInfo.getCertLocalPath());
        String result= "";
        try {
            result = wxPayService.queryOrder("", payOrder.getOutTradeNo()).toString();
        } catch (WxPayException e) {
            e.printStackTrace();
        }
        //输出
        System.out.println("订单查询结果：" +result);

        QueryCreateResult queryCreateResult = new QueryCreateResult();
        queryCreateResult.setPayChannel(payOrder.getPayChannel());
        queryCreateResult.setOutTradeNo(payOrder.getOutTradeNo());
        queryCreateResult.setInfo(result);

        return Result.success(1,"订单查询",queryCreateResult);
    }




    @Override
    public Result<OrderCloseResult> close(OrderCloseRequest request) {
        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria c = example.createCriteria();
        c.andOutTradeNoEqualTo(request.getOutTradeNo());
        List<PayOrder> list = payOrderMapper.selectByExample(example);
        if (list.isEmpty()) {
            return Result.error(-1,"没有查到订单信息") ;
        }

        PayOrder payOrder = list.get(0);


        System.out.println("订单关闭...");

        // 核对支付渠道
        if(!payOrder.getPayChannel().equals(request.getPayChannel())){
            return Result.error(-1,"payChannel错误") ;
        }

        WxPayInfo wxPayInfo = getWxPayInfo(payOrder.getPayChannel());


        WxPayService wxPayService = initWxPayService(wxPayInfo.getAppId(),wxPayInfo.getMchId(),wxPayInfo.getMahKey(),wxPayInfo.getCertLocalPath());


        String result= "";
        try {
            result = wxPayService.closeOrder(payOrder.getOutTradeNo()).toString();
        } catch (WxPayException e) {
            e.printStackTrace();
        }
        //输出
        System.out.println("订单关闭：" +result);

        OrderCloseResult orderCloseResult = new OrderCloseResult();
        orderCloseResult.setPayChannel(payOrder.getPayChannel());
        orderCloseResult.setOutTradeNo(payOrder.getOutTradeNo());
        orderCloseResult.setInfo(result);

        return Result.success(1,"订单关闭",orderCloseResult);
    }

    @Override
    public Result<OrderRefundResult> refund(OrderRefundRequest request) {

        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria c = example.createCriteria();
        c.andOutTradeNoEqualTo(request.getOutTradeNo());
        List<PayOrder> list = payOrderMapper.selectByExample(example);
        if (list.isEmpty()) {
            return Result.error(-1,"没有查到订单信息") ;
        }

        PayOrder payOrder = list.get(0);


        System.out.println("退款...");

        // 核对支付渠道
        if(!payOrder.getPayChannel().equals(request.getPayChannel())){
            return Result.error(-1,"payChannel错误") ;
        }

        WxPayInfo wxPayInfo = getWxPayInfo(payOrder.getPayChannel());


        WxPayService wxPayService = initWxPayService(wxPayInfo.getAppId(),wxPayInfo.getMchId(),wxPayInfo.getMahKey(),wxPayInfo.getCertLocalPath());

        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();
        wxPayRefundRequest.setOutRefundNo(request.getOutRefundNo());
        wxPayRefundRequest.setOutTradeNo(request.getOutTradeNo());
        wxPayRefundRequest.setRefundAccount("REFUND_SOURCE_UNSETTLED_FUNDS");
        wxPayRefundRequest.setRefundDesc(request.getRefundReason());
        wxPayRefundRequest.setRefundFee(Math.toIntExact(request.getRefundAmount()));
        wxPayRefundRequest.setTotalFee(Math.toIntExact(request.getPayAmount()));
        wxPayRefundRequest.setNotifyUrl("xxxxx"); //TODO

        String result = "";
        try {
            result = wxPayService.refund(wxPayRefundRequest).toString();
        } catch (WxPayException e) {
            e.printStackTrace();
        }

        //输出
        System.out.println("退款：" +result);

        OrderRefundResult orderRefundResult = new OrderRefundResult();
        orderRefundResult.setPayChannel(payOrder.getPayChannel());
        orderRefundResult.setOutTradeNo(payOrder.getOutTradeNo());
        orderRefundResult.setInfo(result);

        return Result.success(1,"退款",orderRefundResult);
    }

    @Override
    public Result<RefundQueryResult> refundQuery(RefundQueryRequest request) {
        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria c = example.createCriteria();
        c.andOutTradeNoEqualTo(request.getOutTradeNo());
        List<PayOrder> list = payOrderMapper.selectByExample(example);
        if (list.isEmpty()) {
            return Result.error(-1,"没有查到订单信息") ;
        }

        PayOrder payOrder = list.get(0);


        System.out.println("退款查询...");

        // 核对支付渠道
        if(!payOrder.getPayChannel().equals(request.getPayChannel())){
            return Result.error(-1,"payChannel错误") ;
        }

        WxPayInfo wxPayInfo = getWxPayInfo(payOrder.getPayChannel());


        WxPayService wxPayService = initWxPayService(wxPayInfo.getAppId(),wxPayInfo.getMchId(),wxPayInfo.getMahKey(),wxPayInfo.getCertLocalPath());

        String result = "";
        try {
            result = wxPayService.refundQuery(null, request.getOutTradeNo(), null, null).toString();
        } catch (WxPayException e) {
            e.printStackTrace();
        }

        RefundQueryResult refundQueryResult = new RefundQueryResult();
        refundQueryResult.setPayChannel(payOrder.getPayChannel());
        refundQueryResult.setOutTradeNo(payOrder.getOutTradeNo());
        refundQueryResult.setInfo(result);

        return Result.success(1,"退款查询",refundQueryResult);
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


        String codeURL = "";
        try {
            WxPayUnifiedOrderResult wxPayUnifiedOrderResult = wxPayService.unifiedOrder(wxPayUnifiedOrderRequest);
            codeURL = wxPayUnifiedOrderResult.getCodeURL();

            savePayOrder(payOrder);
        } catch (WxPayException e) {
            e.printStackTrace();
        }

        return codeURL;
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

}
