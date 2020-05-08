package com.foochane.awpay.dao.mapper;

import com.foochane.awpay.dao.entity.AliPayInfo;
import com.foochane.awpay.dao.entity.AliPayInfoExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AliPayInfoMapper {
    long countByExample(AliPayInfoExample example);

    int deleteByExample(AliPayInfoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AliPayInfo record);

    int insertSelective(AliPayInfo record);

    List<AliPayInfo> selectByExample(AliPayInfoExample example);

    AliPayInfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AliPayInfo record, @Param("example") AliPayInfoExample example);

    int updateByExample(@Param("record") AliPayInfo record, @Param("example") AliPayInfoExample example);

    int updateByPrimaryKeySelective(AliPayInfo record);

    int updateByPrimaryKey(AliPayInfo record);
}