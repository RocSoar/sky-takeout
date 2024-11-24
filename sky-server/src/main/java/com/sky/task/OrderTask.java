package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类, 定时处理订单状态
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTask {

    //    * * * * * ? *
//    cron表达式, 有6-7个字段, 用空格分开, *表示该字段不限制时间,(表示每秒/每分钟/每小时... 都会执行一次任务)
//    每个字段分别表示 秒 分 时 日 月 星期几 年
//    其中 日和星期几 为互斥字段, 若指定其中一个, 另一个就要置为不指定(用?表示), 年字段 为可选字段(可不写)
//    常用cron表达式例子:
//    0/2 * * * * ?   表示每2秒 执行任务
//    0 0/2 * * * ?    表示每2分钟 执行任务
//    0 0 2 1 * ?   表示在每月的1日的凌晨2点调整任务
//    0 15 10 ? * MON-FRI   表示周一到周五每天上午10:15执行作业
//    0 15 10 ? 6L 2002-2006   表示2002-2006年的每个月的最后一个星期五上午10:15执行作
//    0 0 10,14,16 * * ?   每天上午10点，下午2点，4点
//    0 0/30 9-17 * * ?   朝九晚五工作时间内每半小时
//    0 0 12 ? * WED    表示每个星期三中午12点
//    0 0 12 * * ?   每天中午12点触发
//    0 15 10 ? * *    每天上午10:15触发
//    0 15 10 * * ?     每天上午10:15触发
//    0 15 10 * * ? 2005    2005年的每天上午10:15触发
//    0 * 14 * * ?     在每天下午2点到下午2:59期间的每1分钟触发
//    0 0/5 14 * * ?    在每天下午2点到下午2:55期间的每5分钟触发
//    0 0/5 14,18 * * ?     在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
//    0 0-5 14 * * ?    在每天下午2点到下午2:05期间的每1分钟触发
//    0 10,44 14 ? 3 WED    每年三月的星期三的下午2:10和2:44触发
//    0 15 10 ? * MON-FRI    周一至周五的上午10:15触发
//    0 15 10 15 * ?    每月15日上午10:15触发
//    0 15 10 L * ?    每月最后一日的上午10:15触发
//    0 15 10 ? * 6L    每月的最后一个星期五上午10:15触发
//    0 15 10 ? * 6L 2002-2005   2002年至2005年的每月的最后一个星期五上午10:15触发
//    0 15 10 ? * 6#3   每月的第三个星期五上午10:15触发

    private final OrderMapper orderMapper;

    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder() {
//        每分钟定时处理超时订单
        log.info("[Scheduled]定时处理超时订单:{}", LocalDateTime.now());

//        当前时间减15分钟, 比这个时间更早的订单就是超时的订单
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15L);
        List<Orders> orders = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);

//        更新订单状态
        for (Orders order : orders) {
            order.setStatus(Orders.CANCELLED);
            order.setCancelReason(Orders.TIMEOUT_CANCEL);
            order.setCancelTime(LocalDateTime.now());
            orderMapper.update(order);
        }
    }

    //   在每天凌晨5点处理一直处于派送中状态的订单
    @Scheduled(cron = "0 0 5 * * ?")
    public void processDeliveryOrder() {
        log.info("[Scheduled]定时处理派送中状态的订单: {}", LocalDateTime.now());

//        当前时间减去5个小时, 只处理今天00:00:00之前的订单
        LocalDateTime time = LocalDateTime.now().plusHours(-5L);
        List<Orders> orders = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);

        for (Orders order : orders) {
            order.setStatus(Orders.COMPLETED);
            order.setDeliveryTime(LocalDateTime.now());
            orderMapper.update(order);
        }
    }
}
