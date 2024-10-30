package com.sky.controller.user;

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
@RestController("userShopController")
@RequestMapping("/user/shop")
@Tag(name = "店铺相关接口")
public class ShopController {
    private final RedisTemplate<String, Integer> redisTemplate;

    public static final String KEY = "SHOP_STATUS";

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
