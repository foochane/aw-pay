package com.foochane.awpay.service;

import com.foochane.awpay.common.request.*;
import com.foochane.awpay.common.result.*;


public interface WeiXinPayService {

    Result<OrderCreateResult> create(OrderCreateRequest request);

    Result<QueryCreateResult> query(OrderQueryRequest request);

    Result<OrderCloseResult> close(OrderCloseRequest request);

    Result<OrderRefundResult> refund(OrderRefundRequest request);

    Result<RefundQueryResult> refundQuery(RefundQueryRequest request);
}
