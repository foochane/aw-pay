package com.foochane.awpay.test.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by foochane on 2020/5/5.
 */
@Configuration
@ConditionalOnClass(WxPayService.class)
public class MyWxPayConfig {

  @Bean
  @ConditionalOnMissingBean
  public WxPayService wxService() {

    WxPayConfig payConfig = new WxPayConfig();
    payConfig.setAppId("xxxxxxxxx");
    payConfig.setMchId("xxxxxxxxx");
    payConfig.setMchKey("xxxxxxxxxxxx");
    payConfig.setKeyPath("/xxx/xxx/apiclient_cert.p12");
    payConfig.setUseSandboxEnv(false); //不使用沙箱环境

    WxPayService wxPayService = new WxPayServiceImpl();
    wxPayService.setConfig(payConfig);
    return wxPayService;
  }

}
