package com.foochane.awpay.config;

/**
 * Created by foochane on 2020/5/2.
 */
public class AlipayConfig {
    // natapp内外网穿透地址
    public static final String natUrl = "http://znznca.natappfree.cc";

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016101800717168";//在后台获取（必须配置）

    // 商户私钥，您的PKCS8格式RSA2私钥 ,使用支付宝平台助手生成
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDK2iWUalv/YnV0jsw2krQd5M7dCla4iwTx2hfqN/FREoXRir1GGRl/9tuqfJUA16y+MMclHZdOJv3snnY8jor5a07rZ9dLeqUfMhurVvpUjc1w5odrDsS7hA48sUu85jhmMtA9IXMwF0oBIMmxAD35Fb4ORZrBkqBOSpgCFq55NClHAEgs/AgQS+HF42KG0GDOK9Qwn/pYdbIDojktc34amO5/GA9Om7QtRQosj+POnXMpzMQMkoOcsu9xewJ0DQtuIbBSIanRqJQUZFg8ZvmM5oZWwcRAKQ1wz/atGw67GbVuonB3tRLMhur8jwZrw7F3KBYqZQHNbyeSD1JoH/GdAgMBAAECggEBAMa3h0LqIm9/O4LQdLstYec7A29ENooYHeLy/KSE4xDBrXgFir+FzRYvdSeBXdJydZXnYN86KsPzrKLE5R0Too8OEUTI9/7ICcQSqUpjLV3xgAUOwDAfSphLmEragFBv02ZCdv67qwS8e9G5FfwgOZWuN/3xL9ZbVlD8AAdjws2Vjgrj7rhRvq3q8J+2Q281TSOTI83TStGOVtQXcIEMqgfK6cKpfNeasGirQQgAglwDJPQxgIMcO9dq5JDCwQE1jzG8Oy5vFGQgZbz/0oEkjGWuNmtZhJGOqwOvC02gEngAzB2g+8UVKKsmeHfj/TqQg5mvhbzJlyHVeD+3+HhFtf0CgYEA6gf2eIOFrZm7kGu4mIBBUeP3zMIADu1f6yjyfJnoKdfAQvIYcC5mF+ohcVjSt/JLE7FNFPKTT8BNNFleTqmfFTmVWCBa3B/DYtnDRUsTRyTGFx3jUxMW4ZkL7Q8q22ynRm9pD+T00xCiEZrejhcvsrvGtGsrVBayItvOHI0f2VcCgYEA3eTq3rKepJFYW/8Fl+i8zybP0HLuR2fwNY+Sw+Y+bSUn5STCymXF12Qg+G4JRFNhv6/qNu++G3JuZlFc8AB1JUOouEAG2uN+C3dN7NSc56J5F3SeFaSqmzIaEUMjUMUEfo/4GcRxdRjdiqfIir4KOBZHRzJ2SuLx8qXdhc+3ECsCgYEAuyXS+1xxsfmV/E+oNouZsJrUtnENCuLYKX19LBcHiS53GRHwaQUxrz4TsEiPeXnQf9bxBSdWlhf5pTsyJ0WNabO6sdZt0km0PYBWnzsCF4pYvnOwLea+DXoOB2xxoTaqLbE6GvPEtGRAtkNaZdY01oFCzVRGOeBMY4PStxJDvdECgYBqoOewrnsHsASa6LjCYAfjL85rZ99TFwJaaqznUIwtHM7IEVs9sZxZvfcOCqqPV9ZALAPL9HcRb3LX3j9f4YhpqNK5/6KLqwhV8DXVGfJwaSXxfIu+8Ms/Gttl8jJVTOe0/fAGSd0PnSxQxgHbkikM/L+x2fD4WYhUxxnlhIR+0wKBgCs/gskmPG2Y17+hcbBT7erxjyTueCow7/NF2zIblSdc/rTbp2Ph81k/oyoGwYNo02R0JZ11ZYxCapDB9XxZDPmgLqJz6U2JauE2bphV3w2Xd01Ph6njiXxSCQ8amGrCtpIHhEWXUubMEihVujurZb9kL6N961cmysSoXJsKGQyC";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.html 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvXYCGNHDSHH8VSSjv8oAcnF7IdPngQG2ym7oZqK3FczgjBCY6U/E8MqkB3EVF6J1wW7v2X+gckfM+RNCET6Fr/VdsJy2wy0TBcaV0IH8E44bP05Esc6SMOX1pAnuXRIhvRo1KcHM00BbE4m3WOzI3zuejyt3kROF/qnm7w/35FcABpI3T4Z4p768W01315kFoL+LHGCamQujNAm+Hz4URUxAPVAFred+2sVwMX96QGlKHaPbu/UTPpgI8xVimTZNO64sqe8Ym6sYRRZiHabqXm7RW9wt5w4n3VPHt6TeKm6S8W9TH4C+86Xn94kaFOHpuj1e5Pr6EYgCFxRNboLtaQIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = natUrl + "/alipay//notify.htm";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = natUrl + "/alipay/return.htm";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";//注意：沙箱测试环境，正式环境为：https://openapi.alipay.com/gateway.do

}
