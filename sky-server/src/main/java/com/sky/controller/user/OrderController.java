package com.sky.controller.user;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.page.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController("userOrderController")
@RequestMapping("/user/order")
@Tag(name = "C端订单接口")
public class OrderController {
    private final OrderService orderService;

    /**
     * 用户下单
     */
    @PostMapping("/submit")
    @Operation(summary = "用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("[C端]用户下单:{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     */
    @PutMapping("/payment")
    @Operation(summary = "订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("[C端]订单支付：{}", ordersPaymentDTO);

        //真实调用微信支付接口, 需要商户号、平台证书等信息
//        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
//        log.info("生成预支付交易单：{}", orderPaymentVO);
//        return Result.success(orderPaymentVO);

//        以下为模拟操作, 跳过了真实调用微信支付接口, 实际上线时不应使用下面的代码
        OrderPaymentVO orderPaymentVO = OrderPaymentVO.builder().nonceStr("模拟支付场景").build();
        log.info("生成预支付交易单：{}", orderPaymentVO);
//        此为模拟操作, 直接调用支付成功方法, 实际要等微信回调这个方法
        orderService.paySuccess(ordersPaymentDTO.getOrderNumber());
        return Result.success(orderPaymentVO);
    }

    /**
     * 历史订单查询
     */
    @GetMapping("/historyOrders")
    @Operation(summary = "历史订单查询")
    public Result<PageResult<OrderVO>> page(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("[C端]历史订单查询:{}", ordersPageQueryDTO);
        PageResult<OrderVO> pageResult = orderService.pageQuery4User(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/orderDetail/{id}")
    @Operation(summary = "查询订单详情")
    public Result<OrderVO> details(@PathVariable Long id) {
        log.info("[C端]查询订单详情:{}", id);
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    /**
     * 用户取消订单
     */
    @PutMapping("/cancel/{id}")
    @Operation(summary = "用户取消订单")
    public Result<Object> cancel(@PathVariable Long id) {
        log.info("[C端]用户取消订单:{}", id);
        orderService.userCancelById(id);
        return Result.success();
    }

    /**
     * 再来一单
     */
    @PostMapping("/repetition/{id}")
    @Operation(summary = "再来一单")
    public Result<Object> repetition(@PathVariable Long id) {
        log.info("[C端]再来一单:{}", id);
        orderService.repetition(id);
        return Result.success();
    }
}
