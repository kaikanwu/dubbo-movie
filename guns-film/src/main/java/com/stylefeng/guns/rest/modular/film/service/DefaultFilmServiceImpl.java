package com.stylefeng.guns.rest.modular.film.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@Service(interfaceClass = FilmServiceApi.class)
public class DefaultFilmServiceImpl implements FilmServiceApi {


    // 注入我们需要的数据层
    @Autowired
    private MoocBannerTMapper moocBannerTMapper;

    @Autowired
    private MoocFilmTMapper moocFilmTMapper;


    @Autowired
    private MoocCatDictTMapper moocCatDictTMapper;

    @Autowired
    private MoocSourceDictTMapper moocSourceDictTMapper;

    @Autowired
    private MoocYearDictTMapper moocYearDictTMapper;

    /**
     * 获取到所有的 banners
     *
     * @return List<BannerVo>
     */
    @Override
    public List<BannerVO> getBanners() {

        List<BannerVO> result = new ArrayList<>();
        List<MoocBannerT> moocBanners = moocBannerTMapper.selectList(null);

        for (MoocBannerT moocBannerT : moocBanners) {

            BannerVO bannerVO = new BannerVO();

            bannerVO.setBannerId(moocBannerT.getUuid() + "");
            bannerVO.setBannerUrl(moocBannerT.getBannerUrl());
            bannerVO.setBannerAddress(moocBannerT.getBannerAddress());

            result.add(bannerVO);
        }
        return result;
    }

    private List<FilmInfo> getFilmInfos(List<MoocFilmT> moocFilms) {
        List<FilmInfo> filmInfos = new ArrayList<>();

        for (MoocFilmT moocFilmT : moocFilms) {
            FilmInfo filmInfo = new FilmInfo();
            filmInfo.setShowTime(DateUtil.getDay(moocFilmT.getFilmTime()));
            filmInfo.setScore(moocFilmT.getFilmScore());
            filmInfo.setImgAddress(moocFilmT.getImgAddress());
            filmInfo.setFilmType(moocFilmT.getFilmType());
            filmInfo.setFilmScore(moocFilmT.getFilmScore());
            filmInfo.setFilmName(moocFilmT.getFilmName());
            filmInfo.setFilmId(String.valueOf(moocFilmT.getUuid()));
            filmInfo.setExpectNum(moocFilmT.getFilmPresalenum());
            filmInfo.setBoxNum(moocFilmT.getFilmBoxOffice());
            // 添加到 List 中
            filmInfos.add(filmInfo);
        }

        return filmInfos;
    }


    /**
     * 获取正在热映的影片
     * @param isLimit
     * @param nums
     * @param nowPage
     * @param sourceId
     * @param yearId
     * @param catId
     * @return
     */
    @Override
    public FilmVO getHotFilms(boolean isLimit, int nums,int nowPage, int sortId,int sourceId, int yearId, int catId) {

        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfos = new ArrayList<>();


        // 热映影片的限制条件
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "1");
        // 1. 判断是否是首页需要的内容
        if (isLimit) {
            // 1.1 如果是则限制条数，限制内容为热映影片
            Page<MoocFilmT> page = new Page<>(1, nums);
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);
            // 组织 filmInfo
            filmInfos = getFilmInfos(moocFilms);
            // 实际拥有的电影可能少于要求的数量，所以这里需要取实际的电影数量
            filmVO.setFilmNum(moocFilms.size());
            filmVO.setFilmInfo(filmInfos);

        } else {
            // 1.2 如果不是，则是列表页，同样需要限制内容为热映影片
            Page<MoocFilmT> page = null;
            // 需要根据 sortId 的不同，来组织不同的 page 对象
            // 1. 按热门排序 2. 按时间排序 3.按评价排序
            switch (sortId){
                case 1:
                    page = new Page<>(nowPage, nums, "film_box_office");
                    break;
                case 2:
                    page = new Page<>(nowPage, nums, "film_time");
                    break;
                case 3:
                    page = new Page<>(nowPage, nums, "film_score");
                    break;
                default:
                    page = new Page<>(nowPage, nums, "film_box_office");
                    break;

            }




            // 如果 sourceId, yearId, catId 不为 99 ，则表示要按照对应的编号进行查询
            if (sourceId != 99) {
                entityWrapper.eq("film_source", sourceId);
            }
            if (yearId != 99) {
                entityWrapper.eq("film_date", yearId);
            }
            if (catId != 99) {
                // 电影类别有点特殊，在于一个电影可能有多种类别，在数据库中的表示为：#2#4#22#
                String catStr = "%#" + catId + "#%";
                entityWrapper.like("film_cats", catStr);
            }

            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);
            // 组织 filmInfo
            filmInfos = getFilmInfos(moocFilms);
            filmVO.setFilmNum(moocFilms.size());

            // 需要总页数 totalCounts / nums +1 ; 例如我们有6条数据，每页显示10条，那么所需要的页数就是 6/10+1 = 1 页
            int totalCounts = moocFilmTMapper.selectCount(entityWrapper);
            int totalPages  = (totalCounts / nums) + 1;



            // 把从数据库得到的数据重新设置到 filmVO 对象上
            filmVO.setFilmInfo(filmInfos);
            filmVO.setTotalPage(totalPages);
            filmVO.setNowPage(nowPage);

        }

        return filmVO;
    }

    /**
     *  获取即将上映的影片
     * @param isLimit
     * @param nums
     * @param nowPage
     * @param sourceId
     * @param yearId
     * @param catId
     * @return
     */
    @Override
    public FilmVO getSoonFilms(boolean isLimit, int nums,int nowPage,int sortId, int sourceId, int yearId, int catId) {

        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfos = new ArrayList<>();


        // 即将上映影片的限制条件
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "2");
        // 1. 判断是否是首页需要的内容
        if (isLimit) {
            // 1.1 如果是则限制条数，限制内容为热映影片
            Page<MoocFilmT> page = new Page<>(1, nums);
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);
            // 组织 filmInfo
            filmInfos = getFilmInfos(moocFilms);
            // 实际拥有的电影可能少于要求的数量，所以这里需要取实际的电影数量
            filmVO.setFilmNum(moocFilms.size());
            filmVO.setFilmInfo(filmInfos);

        } else {

            // 1.2 如果不是，则是列表页，同样需要限制内容为“即将上映影片”
            Page<MoocFilmT> page = null;
            // 需要根据 sortId 的不同，来组织不同的 page 对象
            // 1. 按热门排序 2. 按时间排序 3.按评价排序
            switch (sortId){
                case 1:
                    // 由于影片未上映，所以按照预售数量来做评价
                    page = new Page<>(nowPage, nums, "film_preSaleNum");
                    break;
                case 2:
                    page = new Page<>(nowPage, nums, "film_time");
                    break;
                case 3:
                    page = new Page<>(nowPage, nums, "film_preSaleNum");
                    break;
                default:
                    page = new Page<>(nowPage, nums, "film_preSaleNum");
                    break;

            }

            // 如果 sourceId, yearId, catId 不为 99 ，则表示要按照对应的编号进行查询
            if (sourceId != 99) {
                entityWrapper.eq("film_source", sourceId);
            }
            if (yearId != 99) {
                entityWrapper.eq("film_date", yearId);
            }
            if (catId != 99) {
                // 电影类别有点特殊，在于一个电影可能有多种类别，在数据库中的表示为：#2#4#22#
                String catStr = "%#" + catId + "#%";
                entityWrapper.like("film_cats", catStr);
            }

            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);
            // 组织 filmInfo
            filmInfos = getFilmInfos(moocFilms);
            filmVO.setFilmNum(moocFilms.size());

            // 需要总页数 totalCounts / nums +1 ; 例如我们有6条数据，每页显示10条，那么所需要的页数就是 6/10+1 = 1 页
            int totalCounts = moocFilmTMapper.selectCount(entityWrapper);
            int totalPages  = (totalCounts / nums) + 1;



            // 把从数据库得到的数据重新设置到 filmVO 对象上
            filmVO.setFilmInfo(filmInfos);
            filmVO.setTotalPage(totalPages);
            filmVO.setNowPage(nowPage);
        }

        return filmVO;
    }

    @Override
    public FilmVO getClassicFilms(int nums, int nowPage, int sortId, int sourceId, int yearId, int catId) {

        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfos = new ArrayList<>();

        // “经典影片”的限制条件
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "3");



        // 1.2 如果不是，则是列表页，同样需要限制内容为热映影片
        Page<MoocFilmT> page = null;
        // 需要根据 sortId 的不同，来组织不同的 page 对象
        // 1. 按热门排序 2. 按时间排序 3.按评价排序
        switch (sortId){
            case 1:
                page = new Page<>(nowPage, nums, "film_box_office");
                break;
            case 2:
                page = new Page<>(nowPage, nums, "film_time");
                break;
            case 3:
                page = new Page<>(nowPage, nums, "film_score");
                break;
            default:
                page = new Page<>(nowPage, nums, "film_box_office");
                break;

        }

        // 如果 sourceId, yearId, catId 不为 99 ，则表示要按照对应的编号进行查询
        if (sourceId != 99) {
            entityWrapper.eq("film_source", sourceId);
        }
        if (yearId != 99) {
            entityWrapper.eq("film_date", yearId);
        }
        if (catId != 99) {
            // 电影类别有点特殊，在于一个电影可能有多种类别，在数据库中的表示为：#2#4#22#
            String catStr = "%#" + catId + "#%";
            entityWrapper.like("film_cats", catStr);
        }

        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);
        // 组织 filmInfo
        filmInfos = getFilmInfos(moocFilms);
        filmVO.setFilmNum(moocFilms.size());

        // 需要总页数 totalCounts / nums +1 ; 例如我们有6条数据，每页显示10条，那么所需要的页数就是 6/10+1 = 1 页
        int totalCounts = moocFilmTMapper.selectCount(entityWrapper);
        int totalPages  = (totalCounts / nums) + 1;



        // 把从数据库得到的数据重新设置到 filmVO 对象上
        filmVO.setFilmInfo(filmInfos);
        filmVO.setTotalPage(totalPages);
        filmVO.setNowPage(nowPage);



        return null;
    }


    /**
     * 获取票房排名（首页中显示）
     *
     * @return
     */
    @Override
    public List<FilmInfo> getBoxRanking() {
        // 1. 条件-> 正在上映的前十名
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        // 影片状态,1-正在热映，2-即将上映，3-经典影片
        entityWrapper.eq("film_status", "1");

        // 默认是倒序
        Page<MoocFilmT> page = new Page<>(1, 10, "film_box_office");

        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);

        // 将从数据库提取出来的数据，转换成我们需要的数据
        // 调用 getFilmInfos 这个私有方法来转换
        List<FilmInfo> result = getFilmInfos(moocFilms);

        return result;
    }

    /**
     * 获取预售前十名（首页中显示）
     *
     * @return
     */
    @Override
    public List<FilmInfo> getExpectRanking() {
        // 1. 条件-> 即将上映的，预售前十名
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "2");


        // 默认是倒序
        Page<MoocFilmT> page = new Page<>(1, 10, "film_preSaleNum");

        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);

        // 将从数据库提取出来的数据，转换成我们需要的数据
        // 调用 getFilmInfos 这个私有方法来转换
        List<FilmInfo> result = getFilmInfos(moocFilms);

        return result;
    }

    /**
     * 获取正在上映的评分前十名（首页中显示）
     *
     * @return
     */
    @Override
    public List<FilmInfo> getTop() {
        // 1. 条件 -> 正在上映的，评分前十名
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "1");

        // 默认是倒序
        Page<MoocFilmT> page = new Page<>(1, 10, "film_score");

        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);

        // 将从数据库提取出来的数据，转换成我们需要的数据
        // 调用 getFilmInfos 这个私有方法来转换
        List<FilmInfo> result = getFilmInfos(moocFilms);

        return result;
    }

    /**
     * 实现 getCats 接口的具体内容
     * @return
     */
    @Override
    public List<CatVO> getCats() {

        List<CatVO> catVOS = new ArrayList<>();
        // 1. 查询实体对象 - MoocCatDictT
        //    参数 null 表示，不需要添加条件，即查询所有结果
        List<MoocCatDictT> moocCats = moocCatDictTMapper.selectList(null);

        // 2. 将 实体对象 转换为 业务对象 CatVO
        for (MoocCatDictT moocCat: moocCats) {
            CatVO catVO = new CatVO();
            catVO.setCatName(moocCat.getShowName());
            catVO.setCatId(moocCat.getUuid() + "");
            // 添加到集合
            catVOS.add(catVO);
        }

        return catVOS;
    }


    /**
     *  实现 getSources 接口的具体内容
     * @return
     */
    @Override
    public List<SourceVO> getSources() {
        List<SourceVO> sourceVOS = new ArrayList<>();
        // 1. 查询得到实体对象 MoocSourceDictT
        List<MoocSourceDictT> moocSources = moocSourceDictTMapper.selectList(null);
        // 2. 将数据库中的实体对象转换为 业务层的 VO 对象
        for (MoocSourceDictT moocSource : moocSources) {
            SourceVO sourceVO = new SourceVO();
            sourceVO.setSourceName(moocSource.getShowName());
            sourceVO.setSourceId(moocSource.getUuid() + "");

            sourceVOS.add(sourceVO);
        }

        return sourceVOS;
    }

    /**
     * 实现 getYears 接口的具体内容
     * @return
     */
    @Override
    public List<YearVO> getYears() {

        List<YearVO> yearVOS = new ArrayList<>();
        // 1. 查询实体对象 - MoocYearDictT
        List<MoocYearDictT> moocYears = moocYearDictTMapper.selectList(null);
        // 2. 将实体对象转换为我们在业务层使用的VO 对象 ： YearVO
        for (MoocYearDictT moocYear : moocYears) {
            YearVO yearVO = new YearVO();
            yearVO.setYearName(moocYear.getShowName());
            yearVO.setYearId(moocYear.getUuid() + "");

            yearVOS.add(yearVO);
        }

        return yearVOS;
    }


}