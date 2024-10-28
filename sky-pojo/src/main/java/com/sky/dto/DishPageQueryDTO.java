package com.sky.dto;

import com.sky.page.BasePageQuery;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString(callSuper = true)
public class DishPageQueryDTO extends BasePageQuery implements Serializable {

    //菜品名称
    private String name;

    //分类id
    private Integer categoryId;

    //菜品启售状态 0表示禁用 1表示启用
    private Integer status;

    public DishPageQueryDTO(int page, int pageSize, String name, Integer categoryId, Integer status) {
        super(page, pageSize);
        this.name = name;
        this.categoryId = categoryId;
        this.status = status;
    }
}
