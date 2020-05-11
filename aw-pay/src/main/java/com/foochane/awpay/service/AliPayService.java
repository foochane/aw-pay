package com.foochane.awpay.service;

import com.alipay.api.response.*;
import com.foochane.awpay.common.request.*;
import com.foochane.awpay.common.result.*;


public interface AliPayService {

    Result<AlipayTradePagePayResponse>  create(OrderCreateRequest request);

    Result<AlipayTradeQueryResponse>  query(OrderQueryRequest request);

    Result<AlipayTradeCloseResponse> close(OrderCloseRequest request);

    Result<AlipayTradeRefundResponse> refund(OrderRefundRequest request);

    Result<AlipayTradeFastpayRefundQueryResponse> refundQuery(RefundQueryRequest request);
}
