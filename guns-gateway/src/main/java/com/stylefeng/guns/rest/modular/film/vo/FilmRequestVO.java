package com.stylefeng.guns.rest.modular.film.vo;

import lombok.Data;

@Data
public class FilmRequestVO {


    private Integer showType = 1;
    /* 排序方式：1-按热门搜索，2-按时间搜索，3-按评价搜索*/
    private Integer sortId = 1;
    private Integer sourceId = 99;
    private Integer catId = 99;
    private Integer yearId = 99;
    /* 影片列表的当前页，用于翻页使用*/
    private Integer nowPage = 1;
    /* 每页显示多少条的内容*/
    private Integer pageSize = 18;
}
