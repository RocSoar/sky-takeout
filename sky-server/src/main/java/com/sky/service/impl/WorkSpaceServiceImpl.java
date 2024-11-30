package com.sky.service.impl;

import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkSpaceServiceImpl implements WorkSpaceService {
    private final OrderMapper orderMapper;
    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;

    /**
     * 单日数据统计
     */
    @Override
    public BusinessDataVO getBusinessData(LocalDate date) {
        List<BusinessDataVO> list = orderMapper.getBusinessData(date, date);
        if (list.isEmpty())
            return null;
        return list.get(0);
    }

    /**
     * 订单状态数量概览
     */
    @Override
    public OrderOverViewVO getOverviewOrders() {
        return orderMapper.getOverviewOrders();
    }

    /**
     * 菜品状态概览
     */
    @Override
    public DishOverViewVO getOverviewDishes() {
        return dishMapper.getOverviewDishes();
    }

    /**
     * 套餐状态概览
     */
    @Override
    public SetmealOverViewVO getOverviewSetmeals() {
        return setmealMapper.getOverviewSetmeals();
    }
}
