package com.sky.controller.admin;

import com.sky.constant.StatusConstant;
import com.sky.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Tag(name = "店铺相关接口")
public class ShopController {
    private final RedisTemplate<String, Integer> redisTemplate;

    public static final String KEY = "SHOP_STATUS";

    /**
     * 设置店铺的营业状态
     */
    @PutMapping("/{status}")
    @Operation(summary = "设置店铺的营业状态")
    public Result<Object> setStatus(@PathVariable Integer status) {
        log.info("设置店铺的营业状态为:{}", Objects.equals(status, StatusConstant.ENABLE) ? "营业中" : "打烊中");
        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }

    /**
     * 获取店铺的营业状态
     */
    @GetMapping("/status")
    @Operation(summary = "获取店铺的营业状态")
    public Result<Integer> getStatus() {
        Integer shopStatus = redisTemplate.opsForValue().get(KEY);
        log.info("获取到店铺的营业状态为:{}", Objects.equals(shopStatus, StatusConstant.ENABLE) ? "营业中" : "打烊中");

        return Result.success(shopStatus);
    }
}
