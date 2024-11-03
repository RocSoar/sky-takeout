package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.page.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController("adminSetmealController")
@RequestMapping("/admin/setmeal")
@Tag(name = "套餐管理接口")
public class SetmealController {
    private final SetmealService setmealService;

    /**
     * 根据id查询套餐和对应的套餐菜品关系
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询套餐和对应的套餐菜品关系")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("根据id查询套餐和对应的套餐菜品关系:{}", id);
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    /**
     * 新增套餐和对应的套餐-菜品关系
     */
    @PostMapping
    @Operation(summary = "新增套餐和对应的套餐-菜品关系")
    public Result<Object> addWithSetmealDish(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐和对应的套餐-菜品关系:{}", setmealDTO);
        setmealService.addWithSetmealDish(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐分页查询
     */
    @GetMapping("/page")
    @Operation(summary = "套餐分页查询")
    public Result<PageResult<SetmealVO>> page(SetmealPageQueryDTO pageQueryDTO) {
        log.info("套餐分页查询:{}", pageQueryDTO);
        PageResult<SetmealVO> pageResult = setmealService.page(pageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id修改套餐
     */
    @PutMapping
    @Operation(summary = "根据id修改套餐")
    public Result<Object> update(@RequestBody SetmealDTO setmealDTO) {
        log.info("根据id修改套餐:{}", setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }

    /**
     * 批量删除套餐
     */
    @DeleteMapping
    @Operation(summary = "批量删除套餐")
    public Result<Object> delete(@RequestParam List<Long> ids) {
        log.info("批量删除套餐:{}", ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 启售、停售套餐, status: 0 禁用, 1 启用
     */
    @PostMapping("/status/{status}")
    @Operation(summary = "启售、停售套餐")
    public Result<Object> setStatus(@PathVariable Integer status, Long id) {
        log.info("启售、停售套餐, id:{}, status:{}", id, status);
        setmealService.setStatus(status, id);
        return Result.success();
    }
}
