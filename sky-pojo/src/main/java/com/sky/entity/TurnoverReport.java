package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 每天订单营业额统计
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TurnoverReport {

    // 当天的日期
    private LocalDate orderDate;

    // 当天已完成订单的实收金额总数
    private Double totalAmount;
}
