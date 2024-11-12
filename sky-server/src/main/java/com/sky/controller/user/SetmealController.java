package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Tag(name = "C端-套餐浏览接口")
public class SetmealController {
    private final SetmealService setmealService;

    /**
     * 根据分类id查询套餐
     */
    @Operation(summary = "根据分类id查询套餐")
    @GetMapping("/list")
    @Cacheable(cacheNames = "setmealCache", key = "#categoryId") //key: setmealCache::categoryId
    public Result<List<Setmeal>> getByCategoryId(Long categoryId) {
        log.info("[C端]根据分类id查询套餐:{}", categoryId);
        List<Setmeal> setmeals = setmealService.list(Setmeal.builder().categoryId(categoryId).status(StatusConstant.ENABLE).build());
        return Result.success(setmeals);
    }

    /**
     * 根据套餐id查询包含的菜品
     */
    @Operation(summary = "根据套餐id查询包含的菜品")
    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> getDishItemBySetmealId(@PathVariable Long id) {
        log.info("[C端]根据套餐id查询包含的菜品:{}", id);
        List<DishItemVO> dishItems = setmealService.getDishItemBySetmealId(id);
        return Result.success(dishItems);
    }
}
