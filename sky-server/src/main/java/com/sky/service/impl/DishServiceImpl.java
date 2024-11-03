package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.page.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final SetmealMapper setmealMapper;

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

    /**
     * 根据id查询菜品和对应的口味数据
     */
    @Override
    public DishVO getById(Long id) {
        Dish dish = dishMapper.getById(id);
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    /**
     * 根据id修改菜品---对应的口味数据, 对应的套餐菜品关系数据
     */
    @Override
    @Transactional
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

//        更新套餐-菜品关系表中的菜品名字和价格
        setmealDishMapper.updateByDish(dish);

        Long dishId = dishDTO.getId();
//        根据dishId删除原有的口味数据
        dishFlavorMapper.deleteBatchByDishIds(new ArrayList<>(List.of(dishId)));

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            // 为每一条口味数据赋予对应的菜品id
            flavors.forEach(f -> f.setDishId(dishId));
//        更新口味数据
            dishFlavorMapper.addBatch(flavors);
        }
    }

    /**
     * 启售、停售菜品(关联的套餐), status: 0 禁用, 1 启用
     */
    @Override
    @Transactional
    public void setStatus(Integer status, Long dishId) {
        Dish dish = new Dish();
        dish.setId(dishId);
        dish.setStatus(status);
        dishMapper.update(dish);
        if (Objects.equals(status, StatusConstant.ENABLE)) {
            return;
        }

//        停售菜品时是否要停售关联的套餐??
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishId(dishId);
        if (setmealIds != null && !setmealIds.isEmpty()) {
            log.info("查询到该菜品关联有套餐:{}", setmealIds);
            setmealIds.stream()
                    .map(id -> Setmeal.builder().id(id).status(StatusConstant.DISABLE).build())
                    .forEach(setmealMapper::update);

//            失败的代码---批量更新
//            setmealMapper.updateBatch(setmealList);
////            开启mybatis的批量提交模式
//            try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
//                SetmealMapper setmealMapper = sqlSession.getMapper(SetmealMapper.class);
//                setmealMapper.updateBatch(setmealList);  // 执行批量更新
//                sqlSession.commit();  // 提交批处理
//            } catch (Exception e) {
//                log.error(e.getMessage());
//                // 处理异常（如有必要，可在此进行回滚操作）
//            }
        }
    }

    /**
     * 根据分类id查询菜品
     */
    @Override
    public List<Dish> getByCategoryId(Long categoryId) {
        return dishMapper.getByCategoryId(categoryId);
    }

    /**
     * 根据分类id查询菜品 带有对应的分类名称和对应的口味列表
     */
    @Override
    public List<DishVO> getByCategoryIdWithFlavors(Long categoryId, Integer status) {
        return dishMapper.getByCategoryIdWithCategoryName(categoryId).stream()
                .filter(dishVO -> dishVO.getStatus().equals(status))
                .peek(dishVO -> {
                    List<DishFlavor> flavors = dishFlavorMapper.getByDishId(dishVO.getId());
                    dishVO.setFlavors(flavors);
                }).toList();
    }

    /**
     * 动态条件查询菜品及对应的口味
     */
    @Override
    public List<DishVO> listWithFlavors(Dish dish) {
        return dishMapper.dynamicQuery(dish).stream()
                .map(d -> {
                    DishVO dishVO = new DishVO();
                    BeanUtils.copyProperties(d, dishVO);
                    List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());
                    dishVO.setFlavors(flavors);
                    return dishVO;
                }).toList();
    }


    /**
     * 根据菜品名字查询菜品
     */
    @Override
    public List<Dish> getByDishName(String name) {
        return dishMapper.getByDishName(name);
    }
}
