package com.foochane.awpay.service.Impl;

import com.foochane.awpay.dao.entity.AliPayInfo;
import com.foochane.awpay.dao.entity.AliPayInfoExample;
import com.foochane.awpay.dao.mapper.AliPayInfoMapper;
import com.foochane.awpay.service.AliPayInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AliPayInfoServiceImpl implements AliPayInfoService {

    @Autowired
    private AliPayInfoMapper aliPayInfoMapper;


    @Override
    public AliPayInfo getByPayChannel(String payChannel) {
        AliPayInfoExample example = new AliPayInfoExample();
        AliPayInfoExample.Criteria c = example.createCriteria();
        c.andPayChannelEqualTo(payChannel);
        List<AliPayInfo> list = aliPayInfoMapper.selectByExample(example);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
}
