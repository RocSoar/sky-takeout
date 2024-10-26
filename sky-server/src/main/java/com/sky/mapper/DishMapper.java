package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增菜品
     */
    @AutoFill(OperationType.INSERT)
    void add(Dish dish);

    /**
     * 分页查询总数
     */
    Long count(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 分页查询列表
     */
    List<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询菜品
     */
    @Select("select * from dish where id=#{id}")
    Dish getById(Long id);

    /**
     * 批量删除菜品
     */
    void deleteBatch(List<Long> ids);
}
