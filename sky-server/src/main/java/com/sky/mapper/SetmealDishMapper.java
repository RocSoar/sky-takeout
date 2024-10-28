package com.sky.mapper;

import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品ids查询套餐ids
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 根据套餐id查询对应的套餐-菜品
     */
    @Select("select * from setmeal_dish where setmeal_id=#{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);

    /**
     * 批量添加
     */
    void addBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据菜品id查询套餐ids
     */
    @Select("select setmeal_id from setmeal_dish where dish_id=#{dishId}")
    List<Long> getSetmealIdsByDishId(Long dishId);

    /**
     * 根据菜品实体更新对应的套餐-菜品
     */
    void updateByDish(Dish dish);

    /**
     * 根据套餐ids批量删除套餐菜品关系数据
     */
    void deleteBatchBySetmealIds(List<Long> setmealIds);

    /**
     * 根据套餐id查询菜品ids
     */
    @Select("select dish_id from setmeal_dish where setmeal_id=#{setmealId}")
    List<Long> getDishIdsBySetmealId(Long setmealId);
}
