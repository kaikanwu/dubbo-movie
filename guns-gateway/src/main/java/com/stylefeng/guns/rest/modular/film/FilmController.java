package com.stylefeng.guns.rest.modular.film;


import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/film/")
public class FilmController {


    /**
     * 获取首页信息的接口
     * @return
     */
    @RequestMapping(value = "getIndex", method = RequestMethod.GET)
    public ResponseVO getIndex() {


        // 1. 获取 banner 信息

        // 2. 获取热映影片

        // 3. 获取即将上映的影片

        // 4. 获取票房排行

        // 5. 获取受欢迎榜单

        // 6. 获取前 100 的影片
        return null;

    }


}
