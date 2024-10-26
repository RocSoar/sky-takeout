package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.page.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 新增菜品和对应的口味
     */
    void addWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     */
    PageResult<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     */
    void deleteBatch(List<Long> ids);
}
