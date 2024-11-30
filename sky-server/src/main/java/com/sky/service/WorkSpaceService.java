package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDate;

public interface WorkSpaceService {

    /**
     * 单日数据统计
     */
    BusinessDataVO getBusinessData(LocalDate date);

    /**
     * 订单状态数量概览
     */
    OrderOverViewVO getOverviewOrders();

    /**
     * 菜品状态概览
     */
    DishOverViewVO getOverviewDishes();

    /**
     * 套餐状态概览
     */
    SetmealOverViewVO getOverviewSetmeals();
}
