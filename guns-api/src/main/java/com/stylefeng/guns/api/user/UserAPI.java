package com.stylefeng.guns.api.user;

public interface UserAPI {


    // 登陆接口
    int login(String username, String password);

    // 注册接口
    boolean register(UserModel userModel);

    // 判断用户名是否存在
    boolean checkUserName(String username);

    // 获取用户信息 uuid: unique user id
    UserInfoModel getUserInfo(int uuid);

    // 更新用户信息
    UserInfoModel updateUserInfo(UserInfoModel userInfoModel);



}

