package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/user/shoppingCart")
@Tag(name = "C端-购物车相关接口")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     */
    @PostMapping("/add")
    @Operation(summary = "添加购物车")
    public Result<Object> add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("[C端]添加购物车:{}", shoppingCartDTO);
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 查看购物车
     */
    @GetMapping("/list")
    @Operation(summary = "查看购物车")
    public Result<List<ShoppingCart>> list() {
        log.info("[C端]查看购物车");
        List<ShoppingCart> list = shoppingCartService.list();
        return Result.success(list);
    }

    /**
     * 删除购物车中的一个商品
     */
    @PostMapping("/sub")
    @Operation(summary = "删除购物车中的一个商品")
    public Result<Object> sub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("[C端]删除购物车中的一个商品:{}", shoppingCartDTO);
        shoppingCartService.sub(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clean")
    @Operation(summary = "清空购物车")
    public Result<Object> clean() {
        log.info("[C端]清空购物车");
        shoppingCartService.clean();
        return Result.success();
    }
}
