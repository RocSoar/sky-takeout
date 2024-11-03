package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController("userDishController")
@RequestMapping("/user/dish")
@Tag(name = "C端-菜品浏览接口")
public class DishController {
    private final DishService dishService;

    /**
     * 根据分类id查询菜品
     */
    @GetMapping("/list")
    @Operation(summary = "根据分类id查询菜品")
    public Result<List<DishVO>> getByCategoryId(Long categoryId) {
        log.info("[C端]根据分类id查询菜品,{}", categoryId);

//        此处使用了两种方式实现, 第一种可以携带categoryName字段数据
        List<DishVO> dishVOs = dishService.getByCategoryIdWithFlavors(categoryId, StatusConstant.ENABLE);
//        System.out.println(dishVOs);
//        List<DishVO> dishVOS = dishService.listWithFlavors(Dish.builder().categoryId(categoryId).status(StatusConstant.ENABLE).build());
//        System.out.println(dishVOS);
        return Result.success(dishVOs);
    }
}
