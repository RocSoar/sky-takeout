package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.page.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    /**
     * 根据id查询套餐和对应的套餐菜品关系
     */
    SetmealVO getById(Long id);

    /**
     * 新增套餐和对应的套餐-菜品关系
     */
    void addWithSetmealDish(SetmealDTO setmealDTO);

    /**
     * 套餐分页查询
     */
    PageResult<SetmealVO> page(SetmealPageQueryDTO pageQueryDTO);

    /**
     * 根据id修改套餐
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 批量删除套餐
     */
    void deleteBatch(List<Long> ids);

    /**
     * 启售、停售套餐, status: 0 禁用, 1 启用
     */
    void setStatus(Integer status, Long id);
}
