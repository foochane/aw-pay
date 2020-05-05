package com.foochane.awpay.test.config;

/**
 * Created by foochane on 2020/5/2.
 */
public class MyAliPayConfig {
    // natapp内外网穿透地址
    public static final String natUrl = "http://xxxx.xxxxxxxx.cc";

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "xxxxxxxxx";//在后台获取（必须配置）

    // 商户私钥，您的PKCS8格式RSA2私钥 ,使用支付宝平台助手生成
    public static String merchant_private_key = "xxxxxxxxxxxxxxx";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.html 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "xxxxxxxxxxxxxxxxxxxxxx";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = natUrl + "/alipay/notify.htm";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = natUrl + "/alipay/return.htm";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";//注意：沙箱测试环境，正式环境为：https://openapi.alipay.com/gateway.do

}
