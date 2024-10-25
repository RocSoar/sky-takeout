package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.page.PageResult;
import com.sky.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类管理业务层
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;

    /**
     * 新增分类
     */
    public void add(CategoryDTO categoryDTO) {
        Category category = new Category();
        //属性拷贝
        BeanUtils.copyProperties(categoryDTO, category);

        //分类状态默认为禁用状态0
        category.setStatus(StatusConstant.DISABLE);

        // 获取在当前线程局部变量中存储的用户id
//        设置创建人id和修改人id、创建时间、更新时间
//        以上已全部在AutoFillAspect中实现
        categoryMapper.add(category);
    }

    /**
     * 分页查询
     */
    public PageResult<Category> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        Long total = categoryMapper.count(categoryPageQueryDTO);
        List<Category> records = categoryMapper.page(categoryPageQueryDTO);

        return new PageResult<>(total, records);
    }

    /**
     * 根据id删除分类
     */
    public void deleteById(Long id) {
        //查询当前分类是否关联了菜品，如果关联了就抛出业务异常
        Integer count = dishMapper.countByCategoryId(id);
        if (count > 0) {
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        //查询当前分类是否关联了套餐，如果关联了就抛出业务异常
        count = setmealMapper.countByCategoryId(id);
        if (count > 0) {
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        //删除分类数据
        categoryMapper.deleteById(id);
    }

    /**
     * 修改分类
     */
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        //   设置修改人id、更新时间
//        以上已全部在AutoFillAspect中实现
        categoryMapper.update(category);
    }

    /**
     * 启用、禁用分类, status: 0 禁用, 1 启用
     */
    public void setStatus(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .build();

        //   设置修改人id、更新时间
//        以上已全部在AutoFillAspect中实现
        categoryMapper.update(category);
    }

    /**
     * 根据类型查询分类, 类型: 1 菜品分类, 2 套餐分类
     */
    public List<Category> list(Integer type) {
        return categoryMapper.list(type);
    }
}
