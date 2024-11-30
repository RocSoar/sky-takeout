package com.sky.service.impl;

import com.sky.entity.OrderReport;
import com.sky.entity.Orders;
import com.sky.entity.TurnoverReport;
import com.sky.entity.UserReport;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    /**
     * 统计指定时间区间内的营业额数据
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {

//        性能较好, 只需一次sql查询直接到位
        List<TurnoverReport> turnoverReports = orderMapper.sumAmountGroupByDay(begin, end, Orders.COMPLETED);

        StringJoiner dateListStr = new StringJoiner(",");
        StringJoiner turnoverListStr = new StringJoiner(",");

        for (TurnoverReport turnoverReport : turnoverReports) {
            dateListStr.add(turnoverReport.getOrderDate().toString());
            turnoverListStr.add(Double.toString(turnoverReport.getTotalAmount()));
        }

        return TurnoverReportVO.builder()
                .dateList(dateListStr.toString())
                .turnoverList(turnoverListStr.toString())
                .build();


//        传统写法, 多次sql查询, 性能太差, 不推荐
//        List<LocalDate> dateList = new ArrayList<>();
//        List<Double> turnoverList = new ArrayList<>();

//        while (!begin.isAfter(end)) {
//            dateList.add(begin);
//            begin = begin.plusDays(1L);
//        }
//
//        for (LocalDate date : dateList) {
//            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
//            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
//            Map<Object, Object> map = new HashMap<>();
//            map.put("status", Orders.COMPLETED);
//            map.put("begin", beginTime);
//            map.put("end", endTime);
//            Double turnover = orderMapper.sumByDay(map);
//            turnoverList.add(turnover == null ? 0.0 : turnover);
//        }
//
//        String dateListStr2 = dateList.stream()
//                .map(LocalDate::toString)
//                .collect(Collectors.joining(","));
//
//        String turnoverListStr2 = turnoverList.stream()
//                .map(d -> Double.toString(d))
//                .collect(Collectors.joining(","));
//
//        return TurnoverReportVO.builder()
//                .dateList(dateListStr2)
//                .turnoverList(turnoverListStr2)
//                .build();
    }

    /**
     * 统计指定时间区间内的用户数据
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
//        性能较好, 只需一次sql查询直接到位
        List<UserReport> userReports = userMapper.countIdGroupByDay(begin, end);

        StringJoiner dateList = new StringJoiner(",");
        StringJoiner totalUserList = new StringJoiner(",");
        StringJoiner newUserList = new StringJoiner(",");
        for (UserReport userReport : userReports) {
            dateList.add(userReport.getDate().toString());
            totalUserList.add(userReport.getCumulativeCount().toString());
            newUserList.add(userReport.getCount().toString());
        }

        return UserReportVO.builder()
                .dateList(dateList.toString())
                .newUserList(newUserList.toString())
                .totalUserList(totalUserList.toString())
                .build();
    }

    /**
     * 统计指定时间区间内的订单数据
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
//        性能较好, 只需一次sql查询直接到位
        List<OrderReport> orderReports = orderMapper.countOrderByDay(begin, end);

        Integer rangeValidCount = orderReports.get(0).getRangeValidCount();
        Integer rangeCount = orderReports.get(0).getRangeCount();
        Double orderCompletionRate = rangeCount == 0 ? 0.0 : (double) rangeValidCount / rangeCount;
        StringJoiner dateList = new StringJoiner(",");
        StringJoiner dayCountList = new StringJoiner(",");
        StringJoiner dayValidCountList = new StringJoiner(",");
        for (OrderReport orderReport : orderReports) {
            dateList.add(orderReport.getDate().toString());
            dayCountList.add(orderReport.getDayCount().toString());
            dayValidCountList.add(orderReport.getDayValidCount().toString());
        }

        return OrderReportVO.builder()
                .dateList(dateList.toString())
                .totalOrderCount(rangeCount)
                .validOrderCount(rangeValidCount)
                .orderCompletionRate(orderCompletionRate)
                .orderCountList(dayCountList.toString())
                .validOrderCountList(dayValidCountList.toString())
                .build();
    }

    /**
     * 统计指定时间区间内的销量排名top10
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        List<Map<String, Object>> salesTop = orderMapper.getSalesTop(begin, end, 10);

        StringJoiner nameList = new StringJoiner(",");
        StringJoiner numberList = new StringJoiner(",");
        for (Map<String, Object> map : salesTop) {
            nameList.add(map.get("name").toString());
            numberList.add(map.get("number").toString());
        }
        return SalesTop10ReportVO.builder()
                .nameList(nameList.toString())
                .numberList(numberList.toString())
                .build();
    }
}
