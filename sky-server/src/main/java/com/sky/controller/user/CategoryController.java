package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController("userCategoryController")
@RequestMapping("/user/category")
@Tag(name = "C端-分类接口")
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * 根据类型查询分类, 类型: 1 菜品分类, 2 套餐分类
     */
    @GetMapping("/list")
    @Operation(summary = "根据类型查询分类, 类型: 1 菜品分类, 2 套餐分类")
    public Result<List<Category>> list(@RequestParam(required = false) Integer type) {
        log.info("[C端]根据类型查询分类, type:{}", type);
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
