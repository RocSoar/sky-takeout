package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderNoticeVO {

    //    1来单提醒, 2客户催单
    private Integer type;

    //    订单的主键id
    private Long orderId;

    //    内容
    private String content;
}
