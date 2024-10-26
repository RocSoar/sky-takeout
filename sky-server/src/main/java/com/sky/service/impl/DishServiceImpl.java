package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.page.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 菜品管理业务层
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DishServiceImpl implements DishService {
    private final DishMapper dishMapper;
    private final DishFlavorMapper dishFlavorMapper;
    private final SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品和对应的口味
     */
    @Override
    @Transactional
    public void addWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

//        向菜品表插入一条数据
        dishMapper.add(dish);
//        获取菜品insert语句生成的主键值
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
//            为每一条口味数据赋予对应的菜品id
            flavors.forEach(flavor -> flavor.setDishId(dishId));
//            向口味表插入多条数据
            dishFlavorMapper.addBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     */
    @Override
    public PageResult<DishVO> page(DishPageQueryDTO dishPageQueryDTO) {
        Long total = dishMapper.count(dishPageQueryDTO);
        List<DishVO> records = dishMapper.page(dishPageQueryDTO);

        return new PageResult<>(total, records);
    }

    /**
     * 批量删除菜品
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
//        1.判断当前菜品是否能够删除--是否存在启售中的菜品??
        boolean onSale = ids.stream().anyMatch(id -> Objects.equals(dishMapper.getById(id).getStatus(), StatusConstant.ENABLE));
        //可改造成批量查询的方式提升性能, 而不是在for循环中一个个查询
        if (onSale) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }

//        2.判断当前菜品是否能够删除--是否被套餐关联了??
        List<Long> setMealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
//        此处采用这种方式的原因在于能够极大提高数据库查询性能
//        如果采用遍历ids的方式去一个个id的查询, 性能会很差
//        但这种方式的缺点在于: 这条sql语句几乎不能用于其他用途了,复用性很差
        if (setMealIds != null && !setMealIds.isEmpty()) {
//            当前菜品被套餐关联了, 不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
//        批量删除菜品表中的菜品数据
        dishMapper.deleteBatch(ids);
//        批量删除菜品关联的口味数据
        dishFlavorMapper.deleteBatchByDishIds(ids);
    }
}
