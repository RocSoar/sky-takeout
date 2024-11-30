package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderReport;
import com.sky.entity.Orders;
import com.sky.entity.TurnoverReport;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.OrderVO;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
@SuppressWarnings("rawtypes")
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

    /**
     * 动态条件查询
     */
    List<Orders> list(Orders orders);

    /**
     * 根据订单状态和订单时间查询
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    /**
     * 传统方式查询该天已完成订单的总金额, begin、end、status可以为null
     */
    Double sumByDay(Map map);

    /**
     * 根据指定的日期范围,聚合查询每天已完成订单的总金额,对于当天没有订单的或status不满足的日期,自动把当天营业额置为0,
     * 要求begin、end、status不能为null
     */
    List<TurnoverReport> sumAmountGroupByDay(@NotNull LocalDate begin, @NotNull LocalDate end, @NotNull Integer status);

    /**
     * 根据指定的日期范围, 聚合查询每天已完成订单的总金额, 只可查询当天有订单的日期,对于当天没有订单的或status不满足的日期,直接缺失,无法把当天营业额置为0
     * begin、end、status可以为null
     */
    List<TurnoverReport> sumAmountGroupByExistDay(LocalDate begin, LocalDate end, Integer status);

    /**
     * 根据指定的日期范围, 聚合查询每天的订单总数,有效订单数,日期范围内的订单总数,有效订单数
     */
    List<OrderReport> countOrderByDay(LocalDate begin, LocalDate end);

    /**
     * 统计日期范围内销量排名前<top>的菜品和套餐
     * 不包含套餐中的菜品, 但包含套餐本身
     */
    @MapKey("")
    List<Map<String, Object>> getSalesTop(LocalDate start, LocalDate end, Integer top);

    /**
     * 统计日期范围内销量排名前<top>的菜品
     * 包含套餐中的菜品, 不包含套餐本身
     */
    @MapKey("")
    List<Map<String, Object>> getDishesTop(LocalDate start, LocalDate end, Integer top);

    /**
     * 统计日期范围内销量排名前<top>的套餐
     * 不包含菜品, 仅统计套餐本身
     */
    @MapKey("")
    List<Map<String, Object>> getSetmealsTop(LocalDate start, LocalDate end, Integer top);

    /**
     * 统计日期范围内每一天的营业额、有效订单数、订单完成率、平均客单价、新增用户数;
     * 若参数start = end, 即为查询单日数据
     */
    List<BusinessDataVO> getBusinessData(LocalDate start, LocalDate end);

    /**
     * 查询全部订单数量、待接单数量、待派送数量、已完成数量、已取消数量
     */
    OrderOverViewVO getOverviewOrders();

}
