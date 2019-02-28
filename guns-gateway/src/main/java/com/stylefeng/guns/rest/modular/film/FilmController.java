package com.stylefeng.guns.rest.modular.film;


import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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


    @RequestMapping(value = "getConditionList", method = RequestMethod.GET)
    public ResponseVO getConditionList(@RequestParam(name = "catId",    required = false, defaultValue = "99") String catId,
                                       @RequestParam(name = "sourceId", required = false, defaultValue = "99") String sourceId,
                                       @RequestParam(name = "yearId",   required = false, defaultValue = "99") String yearId) {

        // 1. 类型集合：category

        // 2. 片源集合：source

        // 3. 年代集合：year

        return null;
    }



}
