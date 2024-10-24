package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.page.PageResult;
import java.util.List;

public interface CategoryService {

    /**
     * 新增分类
     */
    void add(CategoryDTO categoryDTO);

    /**
     * 分页查询
     */
    PageResult<Category> page(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据id删除分类
     */
    void deleteById(Long id);

    /**
     * 修改分类
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 启用、禁用分类, status: 0 禁用, 1 启用
     */
    void setStatus(Integer status, Long id);

    /**
     * 根据类型查询分类, 类型: 1 菜品分类, 2 套餐分类
     */
    List<Category> list(Integer type);
}
