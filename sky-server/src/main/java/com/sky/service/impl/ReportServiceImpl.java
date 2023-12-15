package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;
    /**
     * 营业额数据统计
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnover(LocalDate begin, LocalDate end) {

        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Double> list = new ArrayList<>();
        dateList.forEach(date -> {
            LocalDateTime beginTime = LocalDateTime.of(date,LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date,LocalTime.MAX);
            Map map = new HashMap<>();
            map.put("status", Orders.COMPLETED);
            map.put("begin",beginTime);
            map.put("end",endTime);
            Double turnover = orderMapper.sumByMap(map);
            turnover = (turnover == null) ? 0.0 : turnover;
            list.add(turnover);
        });

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(list,","))
                .build();
        /**
         *     //日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
         *     private String dateList;
         *
         *     //营业额，以逗号分隔，例如：406.0,1520.0,75.0
         *     private String turnoverList;
         */
    }

    /**
     * 用户数据统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> newUserList = new ArrayList<>(); //新增用户数
        List<Integer> totalUserList = new ArrayList<>(); //总用户数
        dateList.forEach(date -> {
            LocalDateTime beginTime = LocalDateTime.of(date,LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date,LocalTime.MAX);
            Map map = new HashMap<>();
            map.put("status", Orders.COMPLETED);
            map.put("begin",beginTime);
            map.put("end",endTime);

            Integer newUser = getUserCount(beginTime, endTime);
            //总用户数量 select count(id) from user where  create_time < ?
            Integer totalUser = getUserCount(null, endTime);

            newUser = (newUser == null) ? 0:newUser;
            totalUser = (totalUser == null) ? 0:totalUser;

            newUserList.add(newUser);
            totalUserList.add(totalUser);

        });

        return  UserReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserList,",")).build();
    }



    private Integer getUserCount(LocalDateTime beginTime, LocalDateTime endTime) {

        Map map = new HashMap();
        map.put("begin",beginTime);
        map.put("end",endTime);

        return userMapper.countByMap(map);
    }


    /**
     * 订单数据统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //每天订单总数集合
        List<Integer> orderCountList = new ArrayList<>();
        //每天有效订单数集合
        List<Integer> validOrderCountList = new ArrayList<>();

        dateList.forEach(date -> {
            LocalDateTime beginTime = LocalDateTime.of(date,LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date,LocalTime.MAX);

            //查询每天的总订单数 select count(id) from orders where order_time > ? and order_time < ?
            Integer orderCount = getOrderCount(beginTime, endTime, null);

            //查询每天的有效订单数 select count(id) from orders where order_time > ? and order_time < ? and status = ?
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        });

        //时间区间内的总订单数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).orElse(0);
        //时间区间内的总有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).orElse(0);

        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){
            orderCompletionRate = validOrderCount.doubleValue()/totalOrderCount;
        }

        return  OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(orderCountList," ,"))    //每日订单数
                .validOrderCountList(StringUtils.join(validOrderCountList,",")) //每日有效订单数
                .totalOrderCount(totalOrderCount) // 订单总数
                .validOrderCount(validOrderCount) //有效订单数
                .orderCompletionRate(orderCompletionRate) // 订单完成率
                .build();
    }



    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end,Integer status) {

        Map map = new HashMap();
        map.put("begin",begin);
        map.put("end",end);
        map.put("status",status);
        return orderMapper.countByMap(map);
    }

    /**
     * 销量top10 包括菜品和套餐
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(beginTime, endTime);



        List<String> namelist = goodsSalesDTOList.stream().map(x -> x.getName()).collect(Collectors.toList());
        String nameList = StringUtils.join(namelist, ",");
        List<Integer> numberlist = goodsSalesDTOList.stream().map(x -> x.getNumber()).collect(Collectors.toList());
        String numberList = StringUtils.join(numberlist, ",");
        //静态方法引用：
        //语法：ClassName::staticMethodName
        //示例：Math::abs引用了Math类的静态方法abs。
        //
        //实例方法引用：
        //语法：instance::instanceMethodName
        //示例：String::length引用了String类的实例方法length。
        //
        //类的任意对象方法引用：
        //语法：ClassName::instanceMethodName
        //示例：List::size引用了List类的实例方法size。
        //
        //构造函数引用：
        //语法：ClassName::new
        //示例：ArrayList::new引用了ArrayList的构造函数。
        /**
         *     //商品名称列表，以逗号分隔，例如：鱼香肉丝,宫保鸡丁,水煮鱼
         *     private String nameList;

         *     //销量列表，以逗号分隔，例如：260,215,200
         *     private String numberList;
         */

        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

}
