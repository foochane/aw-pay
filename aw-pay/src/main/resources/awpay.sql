

DROP TABLE if exists `pay_order`;
CREATE TABLE `pay_order` (
  `id` bigint(20) unsigned NOT NULL COMMENT '主键',
  `out_trade_no` varchar(30) NOT NULL COMMENT '商户订单号',
  `pay_type` tinyint(2) NOT NULL COMMENT '支付方式,0-微信支付，1-支付宝支付',
  `pay_amount` bigint(20) NOT NULL COMMENT '支付金额,单位分',
  `currency` varchar(3) NOT NULL DEFAULT 'cny' COMMENT '三位货币代码,人民币:cny',
  `product_id` varchar(20) DEFAULT NULL COMMENT '产品主键ID',
  `subject` varchar(64) NOT NULL COMMENT '商品标题',
  `body` varchar(256) NOT NULL COMMENT '商品描述信息',
  `extra` varchar(512) DEFAULT NULL COMMENT '特定渠道发起时额外参数',
  `out_refund_no` varchar(30)  COMMENT '退款订单号',
  `refund_amount` bigint(20)  COMMENT '退款金额,单位分',
  `refund_reason` varchar(256)  COMMENT '退款原因',
  `status` tinyint(6) NOT NULL DEFAULT '0' COMMENT '支付状态,0-订单生成,1-支付中,2-支付成功,3-退款成功，4-订单关闭，5-业务处理完成',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='支付订单表';

DROP TABLE if exists `ali_pay_info`;
CREATE TABLE `ali_pay_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `pay_channel` varchar(24) NOT NULL COMMENT '支付渠道',
  `app_id` varchar(30) NOT NULL COMMENT '支付宝应用ID',
  `merchant_private_key` varchar(2048) NOT NULL COMMENT '商户私钥',
  `alipay_public_key` varchar(2048) NOT NULL COMMENT '支付宝公钥',
  `notify_url` varchar(1024) NOT NULL COMMENT '同步通知地址',
  `return_url` varchar(1024) NOT NULL COMMENT '异步通知地址',
  `is_sandbox` tinyint(2) NOT NULL DEFAULT '1' COMMENT '是否时沙箱环境,0-否,1-是',
  `remark` varchar(128) DEFAULT NULL COMMENT '备注',
  `status` tinyint(2) NOT NULL DEFAULT '1' COMMENT '渠道状态,0-停止使用,1-使用中',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='支付宝支付信息表';


DROP TABLE if exists `wx_pay_info`;
CREATE TABLE `wx_pay_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `pay_channel` varchar(24) NOT NULL COMMENT '支付渠道',
  `app_id` varchar(30) NOT NULL COMMENT '应用ID',
  `mch_id` varchar(2048) NOT NULL COMMENT '秘钥',
  `mah_key` varchar(2048) NOT NULL COMMENT '支付宝公钥',
  `cert_local_path` varchar(1024) NOT NULL COMMENT '证书地址',
  `remark` varchar(128) DEFAULT NULL COMMENT '备注',
  `status` tinyint(2) NOT NULL DEFAULT '1' COMMENT '渠道状态,0-停止使用,1-使用中',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='微信支付信息表';