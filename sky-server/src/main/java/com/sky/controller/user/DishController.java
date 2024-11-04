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
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate<String, List<DishVO>> redisTemplate;

    /**
     * 根据分类id查询菜品
     */
    @GetMapping("/list")
    @Operation(summary = "根据分类id查询菜品")
    public Result<List<DishVO>> getByCategoryId(Long categoryId) {
        log.info("[C端]根据分类id查询菜品,{}", categoryId);

//        构造redis中的key, 规则: dish_分类id
        String key = "dish_" + categoryId;
//        查询redis中是否存在对应的菜品数据
        List<DishVO> list = redisTemplate.opsForValue().get(key);
        if (list != null && !list.isEmpty()) {
//            如果存在该key, 直接返回, 无需查询数据库
            return Result.success(list);
        }

//        如果不存在该key, 则查询数据库, 将查到的数据放到redis中

//        此处使用了两种方式实现, 第一种可以携带categoryName字段数据
        List<DishVO> dishVOs = dishService.getByCategoryIdWithFlavors(categoryId, StatusConstant.ENABLE);
//        List<DishVO> dishVOS = dishService.listWithFlavors(Dish.builder().categoryId(categoryId).status(StatusConstant.ENABLE).build());

        redisTemplate.opsForValue().set(key, dishVOs);
        return Result.success(dishVOs);
    }
}
