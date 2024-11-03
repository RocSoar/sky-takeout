package com.sky.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    //    微信服务接口地址
    public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    private final WeChatProperties weChatProperties;
    private final UserMapper userMapper;

    /**
     * 微信登录
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
//        调用微信接口服务, 获得当前微信用户的openid
        String openid = getOpenid(userLoginDTO.getCode());

//        判断openid是否为空, 如果为空表示登录失败, 抛出业务异常
        if (openid == null || openid.isBlank())
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);

//       根据openid查用户表, 判断当前用户是否为新用户
        User user = userMapper.getByOpenid(openid);
//        如果是新用户, 自动完成注册
        if (user == null) {
            user = User.builder().openid(openid).createTime(LocalDateTime.now()).build();
            userMapper.add(user);
        }
        return user;
    }

    /**
     * 调用微信接口服务, 获得当前微信用户的openid
     */
    private String getOpenid(String code) {
        HashMap<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN_URL, map);

        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject.getString("openid");
    }
}
