package com.foochane.awpay.dao.mapper;

import com.foochane.awpay.dao.entity.WxPayInfo;
import com.foochane.awpay.dao.entity.WxPayInfoExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WxPayInfoMapper {
    long countByExample(WxPayInfoExample example);

    int deleteByExample(WxPayInfoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(WxPayInfo record);

    int insertSelective(WxPayInfo record);

    List<WxPayInfo> selectByExample(WxPayInfoExample example);

    WxPayInfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") WxPayInfo record, @Param("example") WxPayInfoExample example);

    int updateByExample(@Param("record") WxPayInfo record, @Param("example") WxPayInfoExample example);

    int updateByPrimaryKeySelective(WxPayInfo record);

    int updateByPrimaryKey(WxPayInfo record);
}