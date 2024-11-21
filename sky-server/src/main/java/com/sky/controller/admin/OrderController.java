package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.dto.OrdersStatusDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.page.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@Slf4j
@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Tag(name = "订单管理接口")
public class OrderController {
    private final OrderService orderService;

    /**
     * 订单搜索
     */
    @GetMapping("/conditionSearch")
    @Operation(summary = "订单搜索")
    public Result<PageResult<OrderVO>> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("订单搜索:{}", ordersPageQueryDTO);
        PageResult<OrderVO> pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 各个状态的订单数量统计
     */
    @GetMapping("/statistics")
    @Operation(summary = "各个状态的订单数量统计")
    public Result<OrderStatisticsVO> statistics() {
        log.info("各个状态的订单数量统计");
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 订单详情
     */
    @GetMapping("/details/{id}")
    @Operation(summary = "订单详情")
    public Result<OrderVO> details(@PathVariable Long id) {
        log.info("查询订单详情:{}", id);
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    /**
     * 商家接单
     */
    @PutMapping("/confirm")
    @Operation(summary = "商家接单")
    public Result<Object> confirm(@RequestBody OrdersStatusDTO ordersStatusDTO) {
        log.info("商家接单:{}", ordersStatusDTO);
        ordersStatusDTO.setStatus(Orders.CONFIRMED);
        orderService.updateStatus(ordersStatusDTO);
        return Result.success();
    }

    /**
     * 商家拒单
     */
    @PutMapping("/rejection")
    @Operation(summary = "商家拒单")
    public Result<Object> reject(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("商家拒单:{}", ordersRejectionDTO);
        orderService.reject(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 商家取消订单
     */
    @PutMapping("/cancel")
    @Operation(summary = "商家取消订单")
    public Result<Object> cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("商家取消订单:{}", ordersCancelDTO);
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 商家派送订单
     */
    @PutMapping("/delivery/{id}")
    @Operation(summary = "商家派送订单")
    public Result<Object> delivery(@PathVariable Long id) {
        log.info("商家派送订单:{}", id);
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 商家完成订单
     */
    @PutMapping("/complete/{id}")
    @Operation(summary = "商家完成订单")
    public Result<Object> complete(@PathVariable Long id) {
        log.info("商家完成订单:{}", id);
        orderService.complete(id);
        return Result.success();
    }
}
