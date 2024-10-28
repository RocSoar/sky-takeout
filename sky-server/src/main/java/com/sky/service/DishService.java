package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
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

    /**
     * 根据id查询菜品和对应的口味数据
     */
    DishVO getById(Long id);

    /**
     * 根据id修改菜品和对应的口味数据
     */
    void update(DishDTO dishDTO);

    /**
     * 启售、停售菜品, status: 0 禁用, 1 启用
     */
    void setStatus(Integer status, Long id);

    /**
     * 根据分类id查询菜品
     */
    List<Dish> getByCategoryId(Long categoryId);

    /**
     * 根据菜品名字查询菜品
     */
    List<Dish> getByDishName(String name);
}
