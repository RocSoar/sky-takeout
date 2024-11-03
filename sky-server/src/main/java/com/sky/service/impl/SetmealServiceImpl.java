package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.page.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class SetmealServiceImpl implements SetmealService {
    private final SetmealMapper setmealMapper;
    private final SetmealDishMapper setmealDishMapper;
    private final DishMapper dishMapper;

    /**
     * 根据id查询套餐和对应的套餐菜品关系
     */
    @Override
    public SetmealVO getById(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);

        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 新增套餐和对应的套餐-菜品关系
     */
    @Override
    public void addWithSetmealDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        setmealMapper.add(setmeal);
//        获取insert语句生成的主键值
        Long setmealId = setmeal.getId();

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
//            为每一条套餐-菜品关系赋予对应的套餐id
            setmealDishes.forEach(sd -> sd.setSetmealId(setmealId));
//            向套餐菜品关系表插入多条数据
            setmealDishMapper.addBatch(setmealDishes);
        }
    }

    /**
     * 套餐分页查询
     */
    @Override
    public PageResult<SetmealVO> page(SetmealPageQueryDTO pageQueryDTO) {
        Long total = setmealMapper.count(pageQueryDTO);
        List<SetmealVO> records = setmealMapper.page(pageQueryDTO);

        return new PageResult<>(total, records);
    }

    /**
     * 根据id修改套餐
     */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);

        Long setmealId = setmealDTO.getId();

//        根据套餐id删除原有的套餐菜品关系数据
        setmealDishMapper.deleteBatchBySetmealIds(new ArrayList<>(List.of(setmealId)));

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
//            为每一条套餐-菜品关系赋予对应的套餐id
            setmealDishes.forEach(sd -> sd.setSetmealId(setmealId));
//            更新套餐菜品关系表
            setmealDishMapper.addBatch(setmealDishes);
        }
    }

    /**
     * 批量删除套餐和对应的套餐菜品关系
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //  1.判断当前套餐是否能够删除--是否存在启售中的套餐??
        List<Setmeal> setmeals = setmealMapper.getByIds(ids);

        for (Setmeal setmeal : setmeals) {
            if (Objects.equals(setmeal.getStatus(), StatusConstant.ENABLE))
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }

//        批量删除套餐
        setmealMapper.deleteBatch(ids);
//        批量删除对应的套餐菜品关系
        setmealDishMapper.deleteBatchBySetmealIds(ids);
    }

    /**
     * 启售、停售套餐, status: 0 禁用, 1 启用
     */
    @Override
    public void setStatus(Integer status, Long id) {
//        如果是启售, 判断套餐内所有的菜品是否都被启售了
        if (Objects.equals(status, StatusConstant.ENABLE)) {
            List<Long> dishIds = setmealDishMapper.getDishIdsBySetmealId(id);
            List<Dish> dishes = dishMapper.getByIds(dishIds);
            for (Dish dish : dishes) {
                if (Objects.equals(dish.getStatus(), StatusConstant.DISABLE))
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
//         若没有抛出异常, 则表示套餐内所有菜品都被启售了
        }

        Setmeal setmeal = Setmeal.builder().id(id).status(status).build();
        setmealMapper.update(setmeal);
    }

    /**
     * 根据分类id查询套餐
     */
    @Override
    public List<Setmeal> getByCategoryId(Long categoryId) {
        return setmealMapper.getByCategroryId(categoryId);
    }

    /**
     * 动态条件查询套餐
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        return setmealMapper.dynamicQuery(setmeal);
    }

    /**
     * 根据套餐id查询包含的菜品
     */
    @Override
    public List<DishItemVO> getDishItemBySetmealId(Long setmealId) {
        return setmealMapper.getDishItemBySetmealId(setmealId);
    }
}
