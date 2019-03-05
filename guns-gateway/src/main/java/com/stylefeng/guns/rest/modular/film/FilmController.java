package com.stylefeng.guns.rest.modular.film;


import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.CatVO;
import com.stylefeng.guns.api.film.vo.SourceVO;
import com.stylefeng.guns.api.film.vo.YearVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmConditionVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmRequestVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/film/")
public class FilmController {

    private static final String IMG_PRE = "http://img.meetingshop.cn/";


    @Reference(interfaceClass = FilmServiceApi.class, check = false)
    private FilmServiceApi filmServiceApi;
    /**
     * 获取首页信息的接口
     * @return
     */
    @RequestMapping(value = "getIndex", method = RequestMethod.GET)
    public ResponseVO getIndex() {

        FilmIndexVO filmIndexVO = new FilmIndexVO();
        // 1. 获取 banner 信息
        filmIndexVO.setBanners(filmServiceApi.getBanners());

        // 2. 获取热映影片
        filmIndexVO.setHotFilms(filmServiceApi.getHotFilms(true, 8 ));

        // 3. 获取即将上映的影片
        filmIndexVO.setSoonFilms(filmServiceApi.getSoonFilms(true, 8));

        // 4. 获取票房排行
        filmIndexVO.setBoxRanking(filmServiceApi.getBoxRanking());

        // 5. 获取受欢迎榜单
        filmIndexVO.setExpectRanking(filmServiceApi.getExpectRanking());

        // 6. 获取前 100 的影片
        filmIndexVO.setTop100(filmServiceApi.getTop());


        // 返回获取了数据的 filmIndexVO 对象
        return ResponseVO.success(IMG_PRE, filmIndexVO);

    }


    /**
     * =============================================================
     * 在用户选择条件后，根据对应的条件返回相应的对象集合
     * =============================================================
     *  三个属性默认“非必填”，默认值 99
     * @param catId     条件：分类 ；例如：喜剧，动画，科幻
     * @param sourceId  条件：来源； 例如：大陆，美国，韩国，日本
     * @param yearId    条件： 年代； 例如：2019，2018, 90年代
     * @return
     */
    @RequestMapping(value = "getConditionList", method = RequestMethod.GET)
    public ResponseVO getConditionList(@RequestParam(name = "catId",    required = false, defaultValue = "99") String catId,
                                       @RequestParam(name = "sourceId", required = false, defaultValue = "99") String sourceId,
                                       @RequestParam(name = "yearId",   required = false, defaultValue = "99") String yearId) {

        FilmConditionVO filmConditionVO = new FilmConditionVO();


        //  标识位
        boolean flag = false;
        // 1. 类型集合：category
        List<CatVO> cats = filmServiceApi.getCats();

        List<CatVO> catResult = new ArrayList<>();
        CatVO cat = null;

        for (CatVO catVO : cats) {
            // 1.1 判断集合是否存在 catId, 如果存在，则将对应的实体变成 active 状态

            if (catVO.getCatId().equals("99")) {
                cat = catVO;
                continue;
            }
            if (catVO.getCatId().equals(catId)) {
                flag = true;
                catVO.setActive(true);
            }else {
                catVO.setActive(false);
            }
            catResult.add(catVO);
        }
        // 1.2 如果不存在，则默认将全部变成  active 状态
        if (!flag) {
            // 即没有匹配上
            cat.setActive(true);
            catResult.add(cat);
        }else {
            cat.setActive(false);
            catResult.add(cat);
        }


        // 2. 片源集合：source
        flag = false;
        List<SourceVO> sources = filmServiceApi.getSources();

        List<SourceVO> sourceResult = new ArrayList<>();
        SourceVO source = null;

        for (SourceVO sourceVO : sources) {
            // 1.1 判断集合是否存在 Id, 如果存在，则将对应的实体变成 active 状态

            if (sourceVO.getSourceId().equals("99")) {
                source = sourceVO;
                continue;
            }
            if (sourceVO.getSourceId().equals(sourceId)) {
                flag = true;
                sourceVO.setIsActive(true);
            }else {
                sourceVO.setIsActive(false);
            }
            sourceResult.add(sourceVO);
        }
        // 1.2 如果不存在，则默认将全部变成  active 状态
        if (!flag) {
            // 即没有匹配上
            source.setIsActive(true);
            sourceResult.add(source);
        }else {
            source.setIsActive(false);
            sourceResult.add(source);
        }




        // 3. 年代集合：year
        flag = false;
        List<YearVO> years = filmServiceApi.getYears();

        List<YearVO> yearResult = new ArrayList<>();
        YearVO year = null;

        for (YearVO yearVO : years) {
            // 1.1 判断集合是否存在 Id, 如果存在，则将对应的实体变成 active 状态

            if (yearVO.getYearId().equals("99")) {
                year = yearVO;
                continue;
            }
            if (yearVO.getYearId().equals(yearId)) {
                flag = true;
                yearVO.setActive(true);
            }else {
                yearVO.setActive(false);
            }
            yearResult.add(yearVO);
        }
        // 1.2 如果不存在，则默认将全部变成  active 状态
        if (!flag) {
            // 即没有匹配上
            year.setActive(true);
            yearResult.add(year);
        }else {
            year.setActive(false);
            yearResult.add(year);
        }

        //
        filmConditionVO.setCatInfo(catResult);
        filmConditionVO.setSourceInfo(sourceResult);
        filmConditionVO.setYearInfo(yearResult);

        return ResponseVO.success(filmConditionVO);
    }


    /**
     * 影片查询功能
     * @param filmRequestVO
     * @return
     */
    @RequestMapping(value = "getFilms", method = RequestMethod.GET)
    public ResponseVO getFilms(FilmRequestVO filmRequestVO) {

        // 1. 根据 showType 判断影片查询类型

        // 2. 根据 sortId 排序

        // 3. 添加各种条件查询

        // 4. 判断当前是第几页
        return null;
    }



}
