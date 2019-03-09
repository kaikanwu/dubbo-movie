package com.stylefeng.guns.api.film;

import com.stylefeng.guns.api.film.vo.*;

import java.util.List;

public interface FilmServiceApi {


    // 获取 banners
    List<BannerVO> getBanners();

    // 获取热映影片
    FilmVO getHotFilms(boolean isLimit, int nums, int nowPage, int sortId,int sourceId, int yearId, int catId);

    // 获取即将上映影片
    FilmVO getSoonFilms(boolean isLimit, int nums, int nowPage, int sortId,int sourceId, int yearId, int catId);

    // 获取经典影片
    FilmVO getClassicFilms(int nums, int nowPage, int sortId, int sourceId, int yearId, int catId);

    // 获取票房排行榜
    List<FilmInfo> getBoxRanking();

    // 获取人气排行榜
    List<FilmInfo> getExpectRanking();

    // 获取 Top 100
    List<FilmInfo> getTop();


    // =============接口： 获取影片条件 getConditionList=============

    // 分类 条件
    List<CatVO> getCats();

    // 片源 条件
    List<SourceVO> getSources();

    // 年份 条件
    List<YearVO> getYears();


}
