package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
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

    /**
     * 根据id查询菜品
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id查询菜品, id:{}", id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    /**
     * 根据id修改菜品
     */
    @PutMapping
    @Operation(summary = "根据id修改菜品")
    public Result<Object> update(@RequestBody DishDTO dishDTO) {
        log.info("根据id修改菜品:{}", dishDTO);
        dishService.update(dishDTO);
        return Result.success();
    }

    /**
     * 启售、停售菜品(关联的套餐), status: 0 禁用, 1 启用
     */
    @PostMapping("/status/{status}")
    @Operation(summary = "启售停售菜品(关联的套餐), status: 0 禁用, 1 启用")
    public Result<Object> setStatus(@PathVariable Integer status, Long id) {
        log.info("启售停售菜品,id:{}, status:{}", id, status);
        dishService.setStatus(status, id);
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     */
    @GetMapping(value = "/list", params = "categoryId")
    @Operation(summary = "根据分类id查询菜品")
    public Result<List<Dish>> getByCategoryId(@RequestParam("categoryId") Long categoryId) {
        log.info("根据分类id查询菜品, 分类id:{}", categoryId);
        List<Dish> dishes = dishService.getByCategoryId(categoryId);
        return Result.success(dishes);
    }

    /**
     * 根据菜品名字查询
     */
    @GetMapping(value = "/list", params = "name")
    @Operation(summary = "根据菜品名字查询菜品")
    public Result<List<Dish>> getByDishName(@RequestParam("name") String name) {
        log.info("根据菜品名字查询菜品, {}", name);
        List<Dish> dishes = dishService.getByDishName(name);
        return Result.success(dishes);
    }

}
