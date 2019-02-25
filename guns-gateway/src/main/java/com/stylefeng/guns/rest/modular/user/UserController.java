package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.UserInfoModel;
import com.stylefeng.guns.api.user.UserModel;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user/")
@RestController
public class UserController {


    @Reference(interfaceClass = UserAPI.class)
    private UserAPI userAPI;


    /**
     *  网关里的用户注册功能的实现：通过基本的判断，决定是否传入后端
     *  (后端的实现就是将前端传入的内容转换格式后保存进数据库）
     * @param userModel
     * @return
     */
    @RequestMapping(name = "register", method = RequestMethod.POST)
    public ResponseVO register(UserModel userModel) {
        // 1. 判断用户名是否为空
        // 1.1 这里使用了 trim() 方法， 作用是删除字符串的首尾空白字符
        if (userModel.getUsername() == null || userModel.getUsername().trim().length() == 0) {
            return ResponseVO.serviceFail("用户名不能为空");
        }
        // 2. 判断密码是否为空
        if (userModel.getPassword() == null || userModel.getPassword().trim().length() == 0) {
            return ResponseVO.serviceFail("密码不能为空");
        }

        // 3. 通过上面的验证后，就将 userModel 传给后端
        boolean isSuccess = userAPI.register(userModel);
        if (isSuccess) {
            return ResponseVO.success("注册成功");
        }else {
            return ResponseVO.serviceFail("注册失败");
        }
    }


    /**
     *  检查用户名是否已经存在、为空
     * @param username
     * @return
     */
    @RequestMapping(name = "check", method = RequestMethod.POST)
    public ResponseVO check(String username) {

        if (username!= null && username.trim().length() > 0) {

            // 根据我们写的 checkUserName 方法，当其返回 true 的时候，表示用户名可以使用
            boolean isExists = userAPI.checkUserName(username);
            if (isExists) {
                return ResponseVO.success("用户名不存在");
            }else {
                return ResponseVO.serviceFail("用户名不存在");
            }

        }else {
            return ResponseVO.serviceFail("用户名不能为空！");
        }
    }


    /**
     * 网关：用户退出
     * @return
     */
    @RequestMapping(name = "logout", method = RequestMethod.GET)
    public ResponseVO logout() {

        /*
            实际开发中：
                1. 前端存储 JWT 七天，这期间会涉及 JWT 的刷新
                2. 服务端会存储活跃用户信息 30分钟
                3. JWT里的userId 为 key，查找活跃用户

                需要退出时：
                1. 前端删除掉 JWT
                2. 后端服务器删除活跃用户的缓存

            此次开发中：
                1.前端直接删除掉 JWT
         */

        //由于退出操作在前端发生，所以我们后端直接返回成功（只要能访问到我们这个服务）
        return ResponseVO.success("用户退出成功");
    }


    /**
     * 查询用户信息
     * @return
     */
    @RequestMapping(name = "getUserInfo", method = RequestMethod.GET)
    public ResponseVO getUserInfo() {
        // 1. 获取当前登陆的用户

        String userId = CurrentUser.getCurrentUserId();

        if (userId != null && userId.trim().length() > 0) {
            int uuid = Integer.parseInt(userId);
            UserInfoModel userInfoModel = userAPI.getUserInfo(uuid);
            if (userInfoModel != null) {
                return ResponseVO.success(userInfoModel);
            }else {
                return ResponseVO.appFail("用户信息查询失败");
            }
        }else {
            return ResponseVO.serviceFail("用户未登录");
        }
    }

    /**
     *  修改用户信息
     * @param userInfoModel
     * @return
     */
    @RequestMapping(name = "updateUserInfo", method = RequestMethod.POST)
    public ResponseVO updateUserInfo(UserInfoModel userInfoModel) {

        String userId = CurrentUser.getCurrentUserId();

        if (userId != null && userId.trim().length() > 0) {
            int uuid = Integer.parseInt(userId);

            // 判断当前登陆的 id 和 修改结果的 id 是否相等
            if (uuid != userInfoModel.getUuid()) {
                return ResponseVO.serviceFail("信息不匹配，无法修改");
            }
             UserInfoModel newUserInfoModel = userAPI.updateUserInfo(userInfoModel);

            if (newUserInfoModel != null) {
                return ResponseVO.success(newUserInfoModel);
            }else {
                return ResponseVO.appFail("用户信息修改失败");
            }
        }else {
            return ResponseVO.serviceFail("用户未登录");
        }
    }




}
