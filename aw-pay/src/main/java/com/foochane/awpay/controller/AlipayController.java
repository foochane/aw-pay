package com.foochane.awpay.controller;


import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;

import com.foochane.awpay.common.config.MyAliPayConfig;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by foochane on 2020/5/2.
 */


@RestController
public class AlipayController {


    @ResponseBody
    @RequestMapping(value = "/ali/pay/order/create",method = RequestMethod.GET)
    public void pay (HttpServletRequest httpRequest,
                     HttpServletResponse httpResponse) throws IOException {

        // 1 创建AlipayClient
        AlipayClient alipayClient =  new DefaultAlipayClient(  MyAliPayConfig.gatewayUrl,
                MyAliPayConfig.app_id,
                MyAliPayConfig.merchant_private_key,
                "json",
                MyAliPayConfig.charset,
                MyAliPayConfig.alipay_public_key,
                MyAliPayConfig.sign_type);


        // 2 设置请求参数
        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = "P"+System.currentTimeMillis();
        //付款金额，必填 单位：元
        String total_amount = "80";
        //订单名称，必填
        String subject = "商品购买";
        //商品描述，可空
        String body = "购买商品的描述信息";
        // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
        String timeout_express = "10m";
        AlipayTradePagePayRequest alipayRequest =  new  AlipayTradePagePayRequest(); //创建API对应的request
        alipayRequest.setReturnUrl(MyAliPayConfig.return_url);
        alipayRequest.setNotifyUrl(MyAliPayConfig.notify_url); //在公共参数中设置回跳和通知地址
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

        httpResponse.setContentType( "text/html;charset="  + MyAliPayConfig.charset);
        httpResponse.getWriter().write(form); //直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();

    }

    /**
     * 支付宝同步通知页面
     * return_url必须放在公网上
     */
    @ResponseBody
    @RequestMapping("/ali/pay/return")
    public String returnUrl(HttpServletRequest httpRequest,
                            HttpServletResponse httpResponse) throws Exception {

        System.out.println("支付成功, 进入同步通知接口...");

        // 获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = httpRequest.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }

            // 乱码解决，这段代码在出现乱码时使用
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");

            params.put(name, valueStr);
        }

        // 调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params,
                                                          MyAliPayConfig.alipay_public_key,
                                                          MyAliPayConfig.charset,
                                                          MyAliPayConfig.sign_type);


        // 验签成功
        if (signVerified) {

            // 同步通知返回的参数（部分说明）
            // out_trade_no :	商户订单号
            // trade_no : 支付宝交易号
            // total_amount ： 交易金额
            // auth_app_id/app_id : 商户APPID
            // seller_id ：收款支付宝账号对应的支付宝唯一用户号(商户UID )
            System.out.println("****************** 支付宝同步通知成功   ******************");
            System.out.println("同步通知返回参数：" + params.toString());
            System.out.println("****************** 支付宝同步通知成功   ******************");


        } else {
            System.out.println("支付, 验签失败...");
        }

        // 返回支付操作完成后需要跳转的页面,这里把返回的参数直接传给页面
        return params.toString();
    }

    /**
     * 支付宝服务器异步通知
     * notify_url必须放入公网
     */
    @RequestMapping(value = "/ali/pay/notify")
    @ResponseBody
    public String notify(HttpServletRequest request, HttpServletRequest response) throws Exception {

        System.out.println("支付成功, 进入异步通知接口...");

        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }

            //乱码解决，这段代码在出现乱码时使用
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");

            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params,
                                                          MyAliPayConfig.alipay_public_key,
                                                          MyAliPayConfig.charset,
                                                          MyAliPayConfig.sign_type); //调用SDK验证签名

        if (signVerified) {

            // 异步通知返回的参数（部分说明）
            // out_trade_no ：商户订单号，商户网站订单系统中唯一订单号
            // trade_no ：支付宝交易号，支付宝交易凭证号
            // app_id ：开发者的app_id
            // out_biz_no ：商户业务号
            // buyer_id ：买家支付宝用户号，买家支付宝账号对应的支付宝唯一用户号，以 2088 开头的纯 16 位数字
            // seller_id ：卖家支付宝用户号 卖家支付宝用户号
            // trade_status ：交易状态
            // total_amount ：订单金额
            // receipt_amount ：实收金额
            // invoice_amount ：开票金额
            // buyer_pay_amount ：付款金额
            // point_amount ：集分宝金额 使用集分宝支付的金额，单位为元，精确到小数点后2位
            // subject ：订单标题
            // body ：商品描述
            // fund_bill_list ：支付金额信息
            // passback_params  回传参数
            System.out.println("********************** 支付宝异步通知成功   **********************");
            System.out.println("异步通知返回参数：" + params.toString());
            System.out.println("********************** 支付宝异步通知成功   **********************");


            if (params.get("trade_status").equals("TRADE_FINISHED")){

                // 注意： 注意这里用于退款功能的实现
                System.out.println("执行退款相关业务");
            }else if (params.get("trade_status").equals("TRADE_SUCCESS")) {

                // 1. 根据out_trade_no 查询订单
                // 2. 判断total_amount 是否正确，即是否为商户订单创建时的金额
                // 3. 校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
                // 4. 验证app_id是否为该商户本身
                // 5. 判断该笔订单是否在商户网站中已经做过处理
                // 6. 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序，修改订单状态
                // 7. 如果有做过处理，不执行商户的业务程序
                System.out.println("执行相关业务");
            }

        } else {
            System.out.println("支付, 验签失败...");
        }

        return "success";
    }

    /**
     * 订单查询
     * @param out_trade_no 商户订单号，商户网站订单系统中唯一订单号
     * @param trade_no 支付宝交易号 注意： out_trade_no 和  trade_no 二选一即可
     * @return result
     *
     */
    @ResponseBody
    @RequestMapping(value = "/ali/pay/query")
    public String query(String out_trade_no,String trade_no){

        System.out.println("订单查询...");

        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(MyAliPayConfig.gatewayUrl, MyAliPayConfig.app_id, MyAliPayConfig.merchant_private_key, "json", MyAliPayConfig.charset, MyAliPayConfig.alipay_public_key, MyAliPayConfig.sign_type);

        //设置请求参数
        AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();

        // out_trade_no 和 trade_no 设置二选一即可
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","+"\"trade_no\":\""+ trade_no +"\"}");

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


    /**
     * 退款
     * @param out_trade_no 商户订单号，商户网站订单系统中唯一订单号
     * @param trade_no 支付宝交易号 注意： out_trade_no 和  trade_no 二选一即可
     * @param refund_amount 需要退款的金额，该金额不能大于订单金额，必填
     * @param refund_reason 退款的原因说明
     * @param out_request_no 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传
     * @return result
     *
     */
    @ResponseBody
    @RequestMapping(value = "/ali/pay/refund")
    public String refund(String out_trade_no,String trade_no,String refund_amount, String refund_reason, String out_request_no){

        System.out.println("退款...");

        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(MyAliPayConfig.gatewayUrl, MyAliPayConfig.app_id, MyAliPayConfig.merchant_private_key, "json", MyAliPayConfig.charset, MyAliPayConfig.alipay_public_key, MyAliPayConfig.sign_type);

        //设置请求参数
        AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();


        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"trade_no\":\""+ trade_no +"\","
                + "\"refund_amount\":\""+ refund_amount +"\","
                + "\"refund_reason\":\""+ refund_reason +"\","
                + "\"out_request_no\":\""+ out_request_no +"\"}");

        //请求
        String result = null;
        try {
            result = alipayClient.execute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        //输出
        System.out.println("退款结果:" + result);

        return result;
    }

    /**
     * 退款查询
     * @param out_trade_no  商户订单号，商户网站订单系统中唯一订单号
     * @param trade_no 支付宝交易号， 注意： out_trade_no 和  trade_no 二选一即可
     * @param out_request_no 请求退款接口时，传入的退款请求号，如果在退款请求时未传入，则该值为创建交易时的外部交易号，必填
     * @return result
     */
    @ResponseBody
    @RequestMapping(value = "/ali/pay/refund/query")
    public String refundQuery(String out_trade_no, String trade_no, String out_request_no){
        System.out.println("退款查询......");
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(MyAliPayConfig.gatewayUrl, MyAliPayConfig.app_id, MyAliPayConfig.merchant_private_key, "json", MyAliPayConfig.charset, MyAliPayConfig.alipay_public_key, MyAliPayConfig.sign_type);

        //设置请求参数
        AlipayTradeFastpayRefundQueryRequest alipayRequest = new AlipayTradeFastpayRefundQueryRequest();
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                +"\"trade_no\":\""+ trade_no +"\","
                +"\"out_request_no\":\""+ out_request_no +"\"}");

        //请求
        String result = null;
        try {
            result = alipayClient.execute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        //输出
        System.out.println("退款查询结果："+result);;

        return result;
    }

    /**
     * 交易关闭
     * @param out_trade_no  商户订单号，商户网站订单系统中唯一订单号
     * @param trade_no 支付宝交易号， 注意： out_trade_no 和  trade_no 二选一即可
     * @return result
     */
    @ResponseBody
    @RequestMapping(value = "/ali/pay/close")
    public String close(String out_trade_no, String trade_no){
        System.out.println("交易关闭.....");

        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(MyAliPayConfig.gatewayUrl, MyAliPayConfig.app_id, MyAliPayConfig.merchant_private_key, "json", MyAliPayConfig.charset, MyAliPayConfig.alipay_public_key, MyAliPayConfig.sign_type);


        //设置请求参数
        AlipayTradeCloseRequest alipayRequest = new AlipayTradeCloseRequest();
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\"," +"\"trade_no\":\""+ trade_no +"\"}");

        //请求
        String result = null;
        try {
            result = alipayClient.execute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        //输出
        System.out.println("交易关闭结果："+result);

        return result;
    }


}
