package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 订单统计
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderReport {

    // 当日日期
    private LocalDate date;

    // 当日订单总数
    private Integer dayCount;

    // 当日有效订单数
    private Integer dayValidCount;

    // 日期范围内订单总数
    private Integer rangeCount;

    // 日期范围内有效订单数
    private Integer rangeValidCount;
}
