package com.sky.service.impl;

import com.sky.entity.OrderReport;
import com.sky.entity.Orders;
import com.sky.entity.TurnoverReport;
import com.sky.entity.UserReport;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.utils.POIUtil;
import com.sky.vo.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final POIUtil poiUtil;

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

    /**
     * 导出运营数据报表
     */
    @SneakyThrows
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        BusinessDataVO businessData = orderMapper.getBusinessData(start, end);
        List<BusinessDataVO> rangedBusinessData = orderMapper.getRangedBusinessData(start, end);
//        使用自己封装的POI工具类
        Cell[][] table = poiUtil.getTable();
        CellStyle dateStyle = poiUtil.createDateStyle("yyyy-MM-dd");

        table[1][1].setCellValue("时间: " + start + " 至 " + end);
        table[3][2].setCellValue("¥" + businessData.getTurnover());
        table[3][4].setCellValue(String.format("%.2f%%", businessData.getOrderCompletionRate() * 100)); //转为百分比形式,保留百分比两位小数
        table[3][6].setCellValue(businessData.getNewUsers());
        table[4][2].setCellValue(businessData.getValidOrderCount());
        table[4][4].setCellValue("¥" + businessData.getUnitPrice());

        int startRow = 7;
        for (BusinessDataVO data : rangedBusinessData) {
            int startColumn = 1;
            Cell[] row = table[startRow];

            Cell dateCell = row[startColumn++];
            dateCell.setCellStyle(dateStyle);
            dateCell.setCellValue(data.getDate());
            row[startColumn++].setCellValue(data.getTurnover());
            row[startColumn++].setCellValue(data.getValidOrderCount());
            row[startColumn++].setCellValue(String.format("%.2f%%", data.getOrderCompletionRate() * 100));
            row[startColumn++].setCellValue(data.getUnitPrice());
            row[startColumn].setCellValue(data.getNewUsers());
            startRow++;
        }

        poiUtil.write(response.getOutputStream());

//        获取类路径(resources文件夹)下的文件的输入流
//        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("export/export.xlsx");
    }
}
