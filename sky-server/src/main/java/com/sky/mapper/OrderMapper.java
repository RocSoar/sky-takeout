package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper {

    /**
     * 插入一条订单数据, 并回传主键id
     */
//    @Options(useGeneratedKeys = true, keyProperty = "id")
    void add(Orders orders);

    /**
     * 根据订单号和用户id查询订单
     */
    @Select("select * from orders where number=#{orderNumber} and user_id=#{userId}")
    Orders getByNumberAndUserId(String orderNumber, Long userId);

    /**
     * 修改订单
     */
    void update(Orders orders);

    /**
     * 根据订单号查询订单
     */
    @Select("select * from orders where number=#{orderNumber}")
    Orders getByNumber(String orderNumber);
}
