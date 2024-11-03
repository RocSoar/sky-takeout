package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 根据id查询套餐
     */
    @Select("select * from setmeal where id=#{id}")
    Setmeal getById(Long id);

    /**
     * 新增套餐和对应的套餐-菜品关系
     */
    @AutoFill(OperationType.INSERT)
    void add(Setmeal setmeal);

//    /**
//     * 批量更新套餐
//     */
//    @AutoFill(OperationType.UPDATE_BATCH)
//    void updateBatch(List<Setmeal> setmealList);

    /**
     * 根据id更新套餐
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 分页查询总数
     */
    Long count(SetmealPageQueryDTO pageQueryDTO);

    /**
     * 分页查询列表
     */
    List<SetmealVO> page(SetmealPageQueryDTO pageQueryDTO);

    /**
     * 根据ids批量查询
     */
    List<Setmeal> getByIds(List<Long> ids);

    /**
     * 批量删除
     */
    void deleteBatch(List<Long> ids);
}