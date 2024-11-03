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
     * 根据ids批量查询菜品
     */
    List<Dish> getByIds(List<Long> ids);

    /**
     * 根据id批量删除菜品
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id修改菜品
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据分类id查询菜品
     */
    @Select("select * from dish where category_id=#{categoryId}")
    List<Dish> getByCategoryId(Long categoryId);

    /**
     * 根据菜品名字查询菜品
     */
    @Select("select * from dish where name like concat('%',#{dishName},'%')")
    List<Dish> getByDishName(String dishName);

    /**
     * 根据分类id查询菜品及对应的分类名称
     */
    List<DishVO> getByCategoryIdWithCategoryName(Long categoryId);

    /**
     * 动态条件查询菜品
     */
    List<Dish> dynamicQuery(Dish dish);
}
