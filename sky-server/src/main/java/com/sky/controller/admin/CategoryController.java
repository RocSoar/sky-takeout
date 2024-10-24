package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.page.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/category")
@Tag(name = "分类相关接口")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 新增分类
     */
    @PostMapping
    @Operation(summary = "新增分类")
    public Result<String> save(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类：{}", categoryDTO);
        categoryService.add(categoryDTO);
        return Result.success();
    }

    /**
     * 分类分页查询
     */
    @GetMapping("/page")
    @Operation(summary = "分类分页查询")
    public Result<PageResult<Category>> page(Integer page, Integer pageSize,
                                             @RequestParam(required = false) String name,
                                             @RequestParam(required = false) Integer type) {
        CategoryPageQueryDTO pageQueryDTO = new CategoryPageQueryDTO(page, pageSize, name, type);
        log.info("分类分页查询：{}", pageQueryDTO);
        PageResult<Category> pageResult = categoryService.page(pageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id删除分类
     */
    @DeleteMapping
    @Operation(summary = "删除分类")
    public Result<String> deleteById(Long id) {
        log.info("删除分类：{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改分类
     */
    @PutMapping
    @Operation(summary = "修改分类")
    public Result<String> update(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类: {}", categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     * 启用、禁用分类, status: 0 禁用, 1 启用
     */
    @PostMapping("/status/{status}")
    @Operation(summary = "启用禁用分类, status: 0 禁用, 1 启用")
    public Result<String> setStatus(@PathVariable Integer status, Long id) {
        log.info("启用禁用分类, id:{}, status:{}", id, status);
        categoryService.setStatus(status, id);
        return Result.success();
    }

    /**
     * 根据类型查询分类, 类型: 1 菜品分类, 2 套餐分类
     */
    @GetMapping("/list")
    @Operation(summary = "根据类型查询分类, 类型: 1 菜品分类, 2 套餐分类")
    public Result<List<Category>> list(Integer type) {
        log.info("根据类型查询分类, type:{}", type);
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
