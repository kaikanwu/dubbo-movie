package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 *  影片信息中的 来源
 */
@Data
public class SourceVO implements Serializable {

//    sourceId:”001”,
//		sourceName:”大陆”,
//		isActive: false

    private String sourceId;
    private String sourceName;
    private Boolean isActive;

}
