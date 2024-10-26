package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.page.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/dish")
@Tag(name = "菜品管理接口")
public class DishController {

    private final DishService dishService;

    /**
     * 新增菜品
     */
    @PostMapping
    @Operation(summary = "新增菜品")
    public Result<Object> add(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品:{}", dishDTO);
        dishService.addWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询
     */
    @GetMapping("/page")
    @Operation(summary = "菜品分页查询")
    public Result<PageResult<DishVO>> page(Integer page, Integer pageSize,
                                           @RequestParam(required = false) String name,
                                           @RequestParam(required = false) Integer categoryId,
                                           @RequestParam(required = false) Integer status) {
        DishPageQueryDTO dishPageQueryDTO = new DishPageQueryDTO(page, pageSize, name, categoryId, status);
        log.info("菜品分页查询:{}", dishPageQueryDTO);
        PageResult<DishVO> pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     */
    @DeleteMapping
    @Operation(summary = "批量删除菜品")
    public Result<Object> delete(@RequestParam List<Long> ids) {
        log.info("批量删除菜品:{}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }
}
