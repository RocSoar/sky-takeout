package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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
     * 根据主键id更新订单
     */
    void update(Orders orders);

    /**
     * 根据订单号查询订单
     */
    @Select("select * from orders where number=#{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 分页查询总数
     */
    Long count(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 分页查询列表, 连接address_book表的detail字段, 替换order表的address字段
     */
    List<OrderVO> page(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据主键id查询, 连接address_book表的detail字段, 替换orders表的address字段
     */
    @Select("select o.id, number, status, o.user_id, address_book_id, order_time, " +
            "checkout_time, pay_method, pay_status, amount, remark, o.phone, " +
            "user_name, o.consignee, cancel_reason, rejection_reason, cancel_time, " +
            "estimated_delivery_time, delivery_status, delivery_time, pack_amount, " +
            "tableware_number, tableware_status, ab.detail as address " +
            "from orders o " +
            "left join address_book ab " +
            "on o.address_book_id=ab.id where o.id=#{id}")
    Orders getById(Long id);

    /**
     * 根据状态, 查询数量
     */
    @Select("select count(id) from orders where status=#{status}")
    Integer countStatus(Integer status);
}
