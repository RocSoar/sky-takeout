package com.sky.dto;

import com.sky.page.BasePageQuery;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true)
public class OrdersPageQueryDTO extends BasePageQuery implements Serializable {

    private String number;

    private String phone;

    private Long userId;

    private Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    public OrdersPageQueryDTO(int page, int pageSize, String number, String phone, Long userId, Integer status, LocalDateTime beginTime, LocalDateTime endTime) {
        super(page, pageSize);
        this.number = number;
        this.phone = phone;
        this.userId = userId;
        this.status = status;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }
}
