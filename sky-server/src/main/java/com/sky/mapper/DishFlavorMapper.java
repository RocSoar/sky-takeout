package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量添加口味数据
     */
    void addBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品ids批量删除口味数据
     */
    void deleteBatchByDishIds(List<Long> dishIds);
}
