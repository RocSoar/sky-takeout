package com.sky.mapper;

import com.sky.entity.User;
import com.sky.entity.UserReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     */
    @Select("select * from user where openid=#{openid}")
    User getByOpenid(String openid);

    /**
     * 新增用户
     */
    void add(User user);

    /**
     * 根据主键id查询
     */
    @Select("select * from user where id=#{userId}")
    User getById(Long userId);

    /**
     * 统计指定时间区间内的用户数据
     */
    List<UserReport> countIdGroupByDay(LocalDate begin, LocalDate end);
}
