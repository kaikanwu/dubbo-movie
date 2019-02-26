package com.stylefeng.guns.rest.modular.example;

import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.common.SimpleObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 常规控制器
 *
 * @author fengshuonan
 * @date 2017-08-23 16:02
 */
@Controller
@RequestMapping("/hello")
public class ExampleController {

    @RequestMapping("")
    public ResponseEntity hello() {
//        System.out.println(simpleObject.getUser());

        // userID -> 作为 Key -> 存储在 Redis， userInfo -> 时效为30分钟
        // 第二次访问会先去 Redis中判断
        // 本项目中直接去数据库中取
        System.out.println(CurrentUser.getCurrentUserId());
        return ResponseEntity.ok("请求成功!");
    }
}
