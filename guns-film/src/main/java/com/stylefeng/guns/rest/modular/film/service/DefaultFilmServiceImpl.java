package com.stylefeng.guns.rest.modular.film.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.BannerVO;
import com.stylefeng.guns.api.film.vo.FilmInfo;
import com.stylefeng.guns.api.film.vo.FilmVO;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.common.persistence.dao.MoocBannerTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MoocFilmTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocBannerT;
import com.stylefeng.guns.rest.common.persistence.model.MoocFilmT;
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
     * 正在热映的影片
     *
     * @param isLimit
     * @param nums
     * @return
     */
    @Override
    public FilmVO getHotFilms(boolean isLimit, int nums) {

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

        }

        return filmVO;
    }

    /**
     * 即将上映的影片
     *
     * @param isLimit
     * @param nums
     * @return
     */
    @Override
    public FilmVO getSoonFilms(boolean isLimit, int nums) {

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
            // 1.2 如果不是，则是列表页，同样需要限制内容为热映影片

        }

        return filmVO;
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


}