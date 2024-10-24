package com.sky.dto;

import com.sky.page.BasePageQuery;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString(callSuper = true)
public class EmployeePageQueryDTO extends BasePageQuery implements Serializable {

    //员工姓名
    private String name;

    public EmployeePageQueryDTO(int page, int pageSize, String name) {
        super(page, pageSize);
        this.name = name;
    }
}
