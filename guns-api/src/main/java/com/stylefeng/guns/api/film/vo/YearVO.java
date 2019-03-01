package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 影片信息中的 年份
 */
@Data
public class YearVO implements Serializable {

    /*
        VO: value object 值对象，通常用于业务层之间的数据传递
     */
    private String yearId;
    private String yearName;
    private boolean isActive;
}
