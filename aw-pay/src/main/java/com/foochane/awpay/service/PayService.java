package com.foochane.awpay.service;

import com.foochane.awpay.common.request.*;

public interface PayService {

    String create(OrderCreateRequest request);

    String query(OrderQueryRequest request);

    String close(OrderCloseRequest request);

    String refund(OrderRefundRequest request);

    String refundQuery(RefundQueryRequest request);
}
