package com.sky.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.page.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.BaiduMapUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final AddressBookMapper addressBookMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final UserMapper userMapper;
    private final WeChatPayUtil weChatPayUtil;
    private final BaiduMapUtil baiduMapUtil;

    /**
     * 用户下单
     */
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

        //处理各种业务异常(地址簿为空, 购物车数据为空)
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null)
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);

        String userAddress = addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail();

//        检查用户的收货地址是否超出配送范围
        baiduMapUtil.checkOutOfRange(userAddress);

        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(ShoppingCart.builder().userId(userId).build());
        if (shoppingCartList == null || shoppingCartList.isEmpty())
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);

        //向订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);

        orderMapper.add(orders);

        //向订单明细表插入n条数据
        List<OrderDetail> orderDetailList = shoppingCartList.stream().map(shoppingCart -> {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(orders.getId()); //设置当前订单明细关联的订单id
            return orderDetail;
        }).toList();
        orderDetailMapper.insertBatch(orderDetailList);

        //清空当前用户购物车, 已移至支付成功后
//        shoppingCartMapper.deleteByUserId(userId);

        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount()).build();
    }

    /**
     * 订单支付
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal("0.01"), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException(MessageConstant.ORDER_IS_PAID);
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     */
    @Override
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 判断订单是否存在 ??
        if (ordersDB == null) {
            log.warn("[异常信息]该订单: {} 不存在!", outTradeNo);
            return;
        }

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

//        TODO: 确认能否正确从ThreadLocal中拿到userId ??
        Long userId = BaseContext.getCurrentId();
        log.warn("支付成功后拿到的用户id: {}", userId);

        //清空当前用户购物车
        shoppingCartMapper.deleteByUserId(ordersDB.getUserId());
    }

    /**
     * 用户端订单分页查询
     */
    @Override
    public PageResult<OrderVO> pageQuery4User(OrdersPageQueryDTO ordersPageQueryDTO) {
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());

        // 根据用户id和订单状态进行分页查询
        Long total = orderMapper.count(ordersPageQueryDTO);
        List<OrderVO> orderVOS = orderMapper.page(ordersPageQueryDTO);

        for (OrderVO orderVO : orderVOS) {
            List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderVO.getId());
            orderVO.setOrderDetailList(orderDetails);
        }

        return new PageResult<>(total, orderVOS);
    }

    /**
     * 查询订单详情
     */
    @Override
    public OrderVO details(Long id) {
        Orders orders = orderMapper.getById(id);

        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetails);
        return orderVO;
    }

    /**
     * 用户取消订单
     */
    @SneakyThrows
    @Override
    public void userCancelById(Long id) {
        Orders ordersDB = orderMapper.getById(id);

//        校验订单是否存在
        if (ordersDB == null)
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);

//       * 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        Integer status = ordersDB.getStatus();

        if (status > 2)
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

//        订单处于待接单状态下取消, 需要进行退款
        if (Orders.TO_BE_CONFIRMED.equals(status)) {
//            调用微信支付退款接口, 需要商户号、平台证书等信息, 开发环境暂时先跳过, 生产环境再开放
//            weChatPayUtil.refund(
//                    ordersDB.getNumber(), // 商户订单号
//                    ordersDB.getNumber(), //商户退款单号
//                    new BigDecimal("0.01"), //退款金额, 单位 元
//                    new BigDecimal("0.01") // 原订单金额
//            );
//            支付状态修改为 退款
            orders.setPayStatus(Orders.REFUND);
        }

        //更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(Orders.USER_CANCEL);
        orders.setCancelTime(LocalDateTime.now());
//        根据主键id更新订单
        orderMapper.update(orders);
    }

    /**
     * 再来一单
     */
    @Override
    public void repetition(Long id) {

//        根据订单id查询订单详情
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);

//        将订单详情列表转化为购物车列表
        List<ShoppingCart> shoppingCartList = orderDetails.stream().map(orderDetail -> {
            ShoppingCart cart = new ShoppingCart();
//            拷贝属性, 忽略购物车的id字段
            BeanUtils.copyProperties(orderDetail, cart, "id");
//            构造剩余字段
            cart.setUserId(BaseContext.getCurrentId());
            cart.setCreateTime(LocalDateTime.now());
            return cart;
        }).toList();

//        批量添加购物车
        shoppingCartMapper.addBatch(shoppingCartList);
    }


    /**
     * 条件搜索订单
     */
    @Override
    public PageResult<OrderVO> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {

//        动态条件查询
        Long total = orderMapper.count(ordersPageQueryDTO);
        List<OrderVO> orderVOList = orderMapper.page(ordersPageQueryDTO);

//        为每一个OrderVO设置orderDishes字段
        for (OrderVO orderVO : orderVOList) {
//          根据订单id查询orderDetails
            List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderVO.getId());

            StringBuilder orderDishes = new StringBuilder();
//          将orderDetails列表拼接成orderDishes字符串, 格式: "宫保鸡丁*3;剁椒鱼头*2";
            for (OrderDetail detail : orderDetails) {
                orderDishes.append(detail.getName())
                        .append("*")
                        .append(detail.getNumber())
                        .append(";");
            }
            orderVO.setOrderDishes(orderDishes.toString());
        }

        return new PageResult<>(total, orderVOList);
    }

    /**
     * 各个状态的订单数量统计
     */
    @Override
    public OrderStatisticsVO statistics() {
        //根据状态, 分别查出待接单、已接单/待派送、派送中的订单数量
        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);

        return OrderStatisticsVO.builder()
                .toBeConfirmed(toBeConfirmed)
                .confirmed(confirmed)
                .deliveryInProgress(deliveryInProgress)
                .build();
    }

    /**
     * 更新订单状态
     */
    @Override
    public void updateStatus(OrdersStatusDTO ordersStatusDTO) {
        orderMapper.update(Orders.builder()
                .id(ordersStatusDTO.getId())
                .status(ordersStatusDTO.getStatus())
                .build());
    }

    /**
     * 商家拒单
     */
    @SneakyThrows
    @Override
    public void reject(OrdersRejectionDTO ordersRejectionDTO) {
        Orders ordersDB = orderMapper.getById(ordersRejectionDTO.getId());

//        订单只有存在并且状态为2(待接单) 才可以拒单
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED))
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        Integer payStatus = ordersDB.getPayStatus();
        if (Orders.PAID.equals(payStatus)) {
//            只有用户已支付, 才需要进行退款
//            调用微信支付退款接口, 需要商户号、平台证书等信息, 开发环境暂时先跳过, 生产环境再开放
//            String refund = weChatPayUtil.refund(
//                    ordersDB.getNumber(), // 商户订单号
//                    ordersDB.getNumber(), //商户退款单号
//                    new BigDecimal("0.01"), //退款金额, 单位 元
//                    new BigDecimal("0.01") // 原订单金额
//            );
            log.info("申请退款: ");
//            支付状态修改为 退款
//            orders.setPayStatus(Orders.REFUND);
        }

//        更新订单状态
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());

        orderMapper.update(orders);
    }

    /**
     * 商家取消订单
     */
    @Override
    @SneakyThrows
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        Orders ordersDB = orderMapper.getById(ordersCancelDTO.getId());

        if (ordersDB == null)
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        Integer payStatus = ordersDB.getPayStatus();
        if (Orders.PAID.equals(payStatus)) {
//            只有用户已支付, 才需要进行退款
//            调用微信支付退款接口, 需要商户号、平台证书等信息, 开发环境暂时先跳过, 生产环境再开放
//            String refund = weChatPayUtil.refund(
//                    ordersDB.getNumber(), // 商户订单号
//                    ordersDB.getNumber(), //商户退款单号
//                    new BigDecimal("0.01"), //退款金额, 单位 元
//                    new BigDecimal("0.01") // 原订单金额
//            );
            log.info("申请退款: ");
//            支付状态修改为 退款
//            orders.setPayStatus(Orders.REFUND);
        }

//        更新订单状态
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());

        orderMapper.update(orders);
    }

    /**
     * 商家派送订单
     */
    @Override
    public void delivery(Long id) {
        Orders orders = orderMapper.getById(id);

//        只有状态为3(已接单/待派送)的订单才可以派送
        if (orders == null || !Orders.CONFIRMED.equals(orders.getStatus()))
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);

//        更新订单状态
        orderMapper.update(Orders.builder().id(id).status(Orders.DELIVERY_IN_PROGRESS).build());
    }


    /**
     * 商家完成订单
     */
    @Override
    public void complete(Long id) {
        Orders orders = orderMapper.getById(id);

//        只有状态为4(派送中)的订单才可以完成
        if (orders == null || !Orders.DELIVERY_IN_PROGRESS.equals(orders.getStatus()))
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
//        更新订单状态
        orderMapper.update(Orders.builder()
                .id(id)
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build());
    }

}
