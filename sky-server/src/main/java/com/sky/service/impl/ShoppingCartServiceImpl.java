package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartMapper shoppingCartMapper;
    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;

    /**
     * 添加购物车
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        Long dishId = shoppingCartDTO.getDishId();
        Long setmealId = shoppingCartDTO.getSetmealId();

        // 要求前端必须传dishId或者setmealId其中之一
        if (dishId == null && setmealId == null)
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_PARAM_INCORRECT);


//        构建购物车实体
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .dishId(dishId)
                .setmealId(setmealId)
                .dishFlavor(shoppingCartDTO.getDishFlavor()).build();

        // 查表判断本次要加入购物车的商品是否已经在购物车中存在了
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        //如果已经存在, 将数量加一
        if (list != null && !list.isEmpty()) {
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
            return;
        }

        //如果不存在, 插入一条购物车数据
//        判断本次添加到购物车的是菜品还是套餐
        if (dishId != null) {
//             本次添加到购物车的是菜品
//            查询dish表
            Dish dish = dishMapper.getById(dishId);
//            封装数据
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setAmount(dish.getPrice());
        } else {
//            本次添加到购物车的是套餐
            Setmeal setmeal = setmealMapper.getById(setmealId);
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setAmount(setmeal.getPrice());
        }

        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCartMapper.add(shoppingCart);
    }

    /**
     * 查看购物车
     */
    @Override
    public List<ShoppingCart> list() {
        return shoppingCartMapper.list(ShoppingCart.builder().userId(BaseContext.getCurrentId()).build());
    }

    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }

    /**
     * 删除购物车中的一个商品
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list == null || list.isEmpty())
            return;

        ShoppingCart cart = list.get(0);
        Integer number = cart.getNumber();
        //判断当前商品在购物车中的份数
        if (number == 1) {
            //如果只有一份, 直接删除当前记录
            shoppingCartMapper.deleteById(cart.getId());
        } else if (number > 1) {
            //如果有多份, 把当前份数减一
            cart.setNumber(number - 1);
            shoppingCartMapper.updateNumberById(cart);
        }
    }
}
