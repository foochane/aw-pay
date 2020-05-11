package com.foochane.awpay.service;

import com.foochane.awpay.common.request.*;
import com.foochane.awpay.common.result.*;
import com.github.binarywang.wxpay.bean.result.*;


public interface WeiXinPayService {

    Result<WxPayUnifiedOrderResult> create(OrderCreateRequest request);

    Result<WxPayOrderQueryResult> query(OrderQueryRequest request);

    Result<WxPayOrderCloseResult> close(OrderCloseRequest request);

    Result<WxPayRefundResult> refund(OrderRefundRequest request);

    Result<WxPayRefundQueryResult> refundQuery(RefundQueryRequest request);
}
