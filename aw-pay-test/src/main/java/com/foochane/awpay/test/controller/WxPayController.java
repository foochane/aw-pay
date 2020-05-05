package com.foochane.awpay.test.controller;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.*;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by foochane on 2020/5/5.
 */


@RestController
public class WxPayController {


    @Autowired
    private WxPayService wxPayService;


    /**
     * 添加支付订单
     * @param request 请求参数
     *                至少要包含如下参数，请求示例（扫描支付）：
     *                  {
     *                      "tradeType": "NATIVE",
     *                      "body": "商品购买",
     *                      "outTradeNo": "P22321112130097578901",
     *                      "productId": "12342424242323233",
     *                      "totalFee": 1,
     *                      "spbillCreateIp": "12.3.44.4",
     *                      "notifyUrl":"http://www.xxxx.com:/wx/order/notify"
     *                  }
     * @return 返回支付信息
     * @throws  WxPayException
     */
    @ResponseBody
    @RequestMapping(value = "wx/pay/order/create",method = RequestMethod.POST)
    public WxPayUnifiedOrderResult unifiedOrder(@RequestBody WxPayUnifiedOrderRequest request) throws WxPayException {
        return this.wxPayService.unifiedOrder(request);
    }


    /**
     * 支付回调通知处理
     * @param xmlData
     * @return
     * @throws WxPayException
     */
    @PostMapping("wx/order/notify")
    public String parseOrderNotifyResult(@RequestBody String xmlData) throws WxPayException {
        final WxPayOrderNotifyResult notifyResult = this.wxPayService.parseOrderNotifyResult(xmlData);
        // TODO 根据自己业务场景需要构造返回对象
        return WxPayNotifyResponse.success("成功");
    }

    /**
     * 查询订单
     * @param transactionId 微信订单号
     * @param outTradeNo    商户系统内部的订单号，当没提供transactionId时需要传这个,两个参数二选一即可
     */
    @GetMapping("/wx/par/order/query")
    public WxPayOrderQueryResult queryOrder(@RequestParam(required = false) String transactionId,
                                            @RequestParam(required = false) String outTradeNo)
            throws WxPayException {
        return this.wxPayService.queryOrder(transactionId, outTradeNo);
    }


    /**
     * 申请退款
     * @param request 请求对象
     *                请求示例(至少包含如下参数）：
     *                {
     *                  "outRefundNo": "rxx34343121",
     *                  "outTradeNo": "p22321213009757890",
     *                  "refundAccount": "REFUND_SOURCE_UNSETTLED_FUNDS",
     *                  "refundDesc": "退款",
     *                  "refundFee": 1,
     *                  "totalFee": 1,
     *                  "notifyUrl": "http://www.xxxx.com/wx/notify
     *               }
     * @return 退款操作结果
     */
    @PostMapping("/wx/refund/order/create")
    public WxPayRefundResult refund(@RequestBody WxPayRefundRequest request) throws WxPayException {
        return this.wxPayService.refund(request);
    }

    /**
     * 微信支付-查询退款
     * 以下四个参数四选一
     *
     * @param transactionId 微信订单号
     * @param outTradeNo    商户订单号
     * @param outRefundNo   商户退款单号
     * @param refundId      微信退款单号
     * @return 退款信息
     */
    @GetMapping("/wx/refund/order/query")
    public WxPayRefundQueryResult refundQuery(@RequestParam(required = false) String transactionId,
                                              @RequestParam(required = false) String outTradeNo,
                                              @RequestParam(required = false) String outRefundNo,
                                              @RequestParam(required = false) String refundId)
            throws WxPayException {
        return this.wxPayService.refundQuery(transactionId, outTradeNo, outRefundNo, refundId);
    }

    /**
     * 关闭订单
     * @param outTradeNo 商户系统内部的订单号
     */
    @GetMapping("/wx/order/close{outTradeNo}")
    public WxPayOrderCloseResult closeOrder(@PathVariable String outTradeNo) throws WxPayException {
        return this.wxPayService.closeOrder(outTradeNo);
    }


}
