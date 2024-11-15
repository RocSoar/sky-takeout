package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {

    /**
     * 插入一条订单数据, 并回传主键id
     */
//    @Options(useGeneratedKeys = true, keyProperty = "id")
    void add(Orders orders);
}
