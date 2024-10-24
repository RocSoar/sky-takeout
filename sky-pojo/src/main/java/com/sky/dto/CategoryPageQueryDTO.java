package com.sky.dto;

import com.sky.page.BasePageQuery;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString(callSuper = true)
public class CategoryPageQueryDTO extends BasePageQuery implements Serializable {

    //分类名称
    private String name;

    //分类类型 1菜品分类  2套餐分类
    private Integer type;

    public CategoryPageQueryDTO(int page, int pageSize, String name, Integer type) {
        super(page, pageSize);
        this.name = name;
        this.type = type;
    }
}
