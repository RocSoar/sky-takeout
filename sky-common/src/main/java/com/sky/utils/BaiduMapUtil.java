package com.sky.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.sky.exception.OrderBusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class BaiduMapUtil {

    @Value("${sky.shop.address}")
    private String shopAddress;

    @Value("${sky.baidu-map.ak}")
    private String ak;

    public static final String GEO_URL = "https://api.map.baidu.com/geocoding/v3";
    public static final String DIRECTION_URL = "https://api.map.baidu.com/directionlite/v1/driving";

    /**
     * 检查客户的收货地址是否超出配送范围
     */
    public void checkOutOfRange(String address) {
        log.info("地址: {}", address);

        Map<String, String> map = new HashMap<>();
        map.put("address", shopAddress);
        map.put("output", "json");
        map.put("ak", ak);

//        获取店铺的经纬度坐标
        String shopCoordinate = HttpClientUtil.doGet(GEO_URL, map);

        JSONObject jsonObject = JSON.parseObject(shopCoordinate);
        if (!jsonObject.getString("status").equals("0"))
            throw new OrderBusinessException("店铺地址解析失败");

//        数据解析
        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        String lat = location.getString("lat");//纬度 latitude
        String lng = location.getString("lng");//经度 longitude
//        店铺经纬度坐标
        String shopCoord = lat + "," + lng;

        map.put("address", address);
//        获取用户收货地址经纬度坐标
        String userCoordinate = HttpClientUtil.doGet(GEO_URL, map);

        JSONObject jsonObject2 = JSON.parseObject(userCoordinate);
        if (!jsonObject2.getString("status").equals("0"))
            throw new OrderBusinessException("用户收货地址解析失败");


        //   数据解析
        JSONObject res = jsonObject2.getJSONObject("result");
        JSONObject location2 = res.getJSONObject("location");
        log.info("地址级别: {}", res.getString("level"));
        String lat2 = location2.getString("lat");//纬度 latitude
        String lng2 = location2.getString("lng");//经度 longitude
//        用户收货地址经纬度坐标
        String userCoord = lat2 + "," + lng2;

        map.put("origin", shopCoord);
        map.put("destination", userCoord);
        map.put("steps_info", "0");

//        路线规划
        String json = HttpClientUtil.doGet(DIRECTION_URL, map);

        JSONObject jsonObject3 = JSON.parseObject(json);
        if (!jsonObject3.getString("status").equals("0"))
            throw new OrderBusinessException("配送路线规划失败");

//        数据解析
        JSONObject result = jsonObject3.getJSONObject("result");
        JSONArray routes = result.getJSONArray("routes");
        Integer distance = routes.getJSONObject(0).getInteger("distance");

        log.info("配送距离: {}米", distance);

//        配送距离超过5000米
        if (distance > 5000)
            throw new OrderBusinessException("超出配送范围(>5000米)");
    }
}
