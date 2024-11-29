package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 用户数据统计
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserReport {

    // 当天日期
    private LocalDate date;

    // 当天新增用户数
    private Long count;

    // 截止到当天累计总用户数
    private Long cumulativeCount;
}
