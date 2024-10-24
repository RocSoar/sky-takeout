package com.sky.page;


import lombok.Data;

@Data
public class BasePageQuery {
    //页码
    protected int page;

    //每页记录数
    protected int pageSize;

    //由页码计算出数据库条目起始索引
    protected int startIndex;

    public BasePageQuery(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
        this.startIndex = (page - 1) * pageSize;
    }

}