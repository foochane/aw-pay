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
import com.github.binarywang.wxpay.bean.result.*;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;


@Service
public class WeiXinPayServiceImpl implements WeiXinPayService {


    @Resource
    private WxPayInfoMapper wxPayInfoMapper;

    @Resource
    private PayOrderMapper payOrderMapper;

    @Override
    public Result<WxPayUnifiedOrderResult> create(OrderCreateRequest request) {
        String payChannel = request.getPayChannel();
        PayOrder payOrder = new PayOrder();
        payOrder.setOutTradeNo("P" + IdWorker.getId());
        payOrder.setProductId(request.getProductId());
        payOrder.setSubject(request.getSubject());
        payOrder.setBody(request.getBody());
        payOrder.setPayAmount(request.getPayAmount());
        payOrder.setPayChannel(payChannel);
        payOrder.setStatus((byte) 0); // 支付状态,0-生成支付订单,1-支付成功,2-退款成功，3-订单关闭，4-业务处理完成
        switch (payChannel) {
            case "WXPAY_NATIVE":
                return Result.success(1,"生成订单",wxPayOrderCreate(payOrder));
            case "WXPAY_WAP":
                return null;
            default:
                return Result.error(-1, "不支持此支付方式");
        }
    }



    @Override
    public Result<WxPayOrderQueryResult> query(OrderQueryRequest request) {

        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria c = example.createCriteria();
        c.andOutTradeNoEqualTo(request.getOutTradeNo());
        List<PayOrder> list = payOrderMapper.selectByExample(example);
        if (list.isEmpty()) {
            return Result.error(-1,"没有查到订单信息") ;
        }

        PayOrder payOrder = list.get(0);


        WxPayInfo wxPayInfo = getWxPayInfo(payOrder.getPayChannel());

        if(ObjectUtils.isEmpty(wxPayInfo)){
            return Result.error(-1,"支付渠道错误,该订单支付渠道为：" + payOrder.getPayChannel());
        }

        WxPayService wxPayService = initWxPayService(wxPayInfo.getAppId(),wxPayInfo.getMchId(),wxPayInfo.getMahKey(),wxPayInfo.getCertLocalPath());

        try {
            WxPayOrderQueryResult result = wxPayService.queryOrder("", payOrder.getOutTradeNo());
            return Result.success(1,"订单查询",result);
        } catch (WxPayException e) {
            e.printStackTrace();
        }

        return Result.error(-1, "操作失败");
    }




    @Override
    public Result<WxPayOrderCloseResult> close(OrderCloseRequest request) {
        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria c = example.createCriteria();
        c.andOutTradeNoEqualTo(request.getOutTradeNo());
        List<PayOrder> list = payOrderMapper.selectByExample(example);
        if (list.isEmpty()) {
            return Result.error(-1,"没有查到订单信息") ;
        }

        PayOrder payOrder = list.get(0);


        System.out.println("订单关闭...");

        WxPayInfo wxPayInfo = getWxPayInfo(payOrder.getPayChannel());

        if(ObjectUtils.isEmpty(wxPayInfo)){
            return Result.error(-1,"支付渠道错误,该订单支付渠道为：" + payOrder.getPayChannel());
        }

        WxPayService wxPayService = initWxPayService(wxPayInfo.getAppId(),wxPayInfo.getMchId(),wxPayInfo.getMahKey(),wxPayInfo.getCertLocalPath());

        try {
            WxPayOrderCloseResult result = wxPayService.closeOrder(payOrder.getOutTradeNo());
            return Result.success(1,"订单关闭",result);
        } catch (WxPayException e) {
            e.printStackTrace();
        }

        return Result.error(-1, "操作失败");
    }

    @Override
    public Result<WxPayRefundResult> refund(OrderRefundRequest request) {

        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria c = example.createCriteria();
        c.andOutTradeNoEqualTo(request.getOutTradeNo());
        List<PayOrder> list = payOrderMapper.selectByExample(example);
        if (list.isEmpty()) {
            return Result.error(-1,"没有查到订单信息") ;
        }

        PayOrder payOrder = list.get(0);


        System.out.println("退款...");


        WxPayInfo wxPayInfo = getWxPayInfo(payOrder.getPayChannel());

        if(ObjectUtils.isEmpty(wxPayInfo)){
            return Result.error(-1,"支付渠道错误,该订单支付渠道为：" + payOrder.getPayChannel());
        }

        WxPayService wxPayService = initWxPayService(wxPayInfo.getAppId(),wxPayInfo.getMchId(),wxPayInfo.getMahKey(),wxPayInfo.getCertLocalPath());

        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();
        wxPayRefundRequest.setOutRefundNo(request.getOutRefundNo());
        wxPayRefundRequest.setOutTradeNo(request.getOutTradeNo());
        wxPayRefundRequest.setRefundAccount("REFUND_SOURCE_UNSETTLED_FUNDS");
        wxPayRefundRequest.setRefundDesc(request.getRefundReason());
        wxPayRefundRequest.setRefundFee(Math.toIntExact(request.getRefundAmount()));
        wxPayRefundRequest.setTotalFee(Math.toIntExact(request.getPayAmount()));
        wxPayRefundRequest.setNotifyUrl("xxxxx"); //TODO


        try {
            WxPayRefundResult result = wxPayService.refund(wxPayRefundRequest);
            return Result.success(1,"退款",result);
        } catch (WxPayException e) {
            e.printStackTrace();
        }

        return Result.error(-1, "操作失败");
    }

    @Override
    public Result<WxPayRefundQueryResult> refundQuery(RefundQueryRequest request) {
        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria c = example.createCriteria();
        c.andOutTradeNoEqualTo(request.getOutTradeNo());
        List<PayOrder> list = payOrderMapper.selectByExample(example);
        if (list.isEmpty()) {
            return Result.error(-1,"没有查到订单信息") ;
        }

        PayOrder payOrder = list.get(0);


        System.out.println("退款查询...");


        WxPayInfo wxPayInfo = getWxPayInfo(payOrder.getPayChannel());

        if(ObjectUtils.isEmpty(wxPayInfo)){
            return Result.error(-1,"支付渠道错误,该订单支付渠道为：" + payOrder.getPayChannel());
        }

        WxPayService wxPayService = initWxPayService(wxPayInfo.getAppId(),wxPayInfo.getMchId(),wxPayInfo.getMahKey(),wxPayInfo.getCertLocalPath());

        try {
            WxPayRefundQueryResult result = wxPayService.refundQuery(null, request.getOutTradeNo(), null, null);
            return Result.success(1,"退款查询",result);
        } catch (WxPayException e) {
            e.printStackTrace();
        }

        return Result.error(-1, "操作失败");
    }




    private WxPayUnifiedOrderResult wxPayOrderCreate(PayOrder payOrder) {

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


        try {
            WxPayUnifiedOrderResult wxPayUnifiedOrderResult = wxPayService.unifiedOrder(wxPayUnifiedOrderRequest);
            savePayOrder(payOrder);
            return wxPayUnifiedOrderResult;
        } catch (WxPayException e) {
            e.printStackTrace();
        }

        return null;
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
