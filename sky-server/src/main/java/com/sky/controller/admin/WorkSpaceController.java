package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/workspace")
@Tag(name = "工作台接口")
public class WorkSpaceController {
    private final WorkSpaceService workSpaceService;

    /**
     * 今日数据统计
     */
    @GetMapping("/businessData")
    @Operation(summary = "今日数据统计")
    public Result<BusinessDataVO> getBusinessData() {
        LocalDate date = LocalDate.now();
        log.info("今日数据统计: {}", date);
        BusinessDataVO businessDataVO = workSpaceService.getBusinessData(date);
        return Result.success(businessDataVO);
    }

    /**
     * 订单状态数量概览
     */
    @GetMapping("/overviewOrders")
    @Operation(summary = "订单状态数量概览")
    public Result<OrderOverViewVO> getOverviewOrders() {
        log.info("订单状态数量概览");
        OrderOverViewVO orderOverViewVO = workSpaceService.getOverviewOrders();
        return Result.success(orderOverViewVO);
    }

    /**
     * 菜品状态概览
     */
    @GetMapping("/overviewDishes")
    @Operation(summary = "菜品状态概览")
    public Result<DishOverViewVO> getOverviewDishes() {
        log.info("菜品状态概览");
        DishOverViewVO dishOverViewVO = workSpaceService.getOverviewDishes();
        return Result.success(dishOverViewVO);
    }

    /**
     * 套餐状态概览
     */
    @GetMapping("/overviewSetmeals")
    @Operation(summary = "套餐状态概览")
    public Result<SetmealOverViewVO> getOverviewSetmeals() {
        log.info("套餐状态概览");
        SetmealOverViewVO setmealOverViewVO = workSpaceService.getOverviewSetmeals();
        return Result.success(setmealOverViewVO);
    }
}
