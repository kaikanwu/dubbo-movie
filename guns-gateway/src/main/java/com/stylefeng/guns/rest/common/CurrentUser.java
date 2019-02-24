package com.stylefeng.guns.rest.common;

import com.stylefeng.guns.api.user.UserInfoModel;

/**
 *  1. 使用 ThreadLocal 来存储 UserInfo；但是不推荐使用，因为会在每个线程中都创建，如果 ThreadLocal 中的数据量很大，就会影响性能
 *  2. 这里使用 UserId 来代表用户信息
 */
public class CurrentUser {

    // 线程绑定的存储空间
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    /**
     *  保存用户的 ID
     * @param userId
     */
    public static void saveUserId(String userId) {
        threadLocal.set(userId);
    }

    /**
     * 获取用户的 ID
     * @return
     */
    public static String getCurrentUser() {
        return threadLocal.get();
    }



//
//    /**
//     * 保存 UserInfo
//     * @param userInfoModel
//     */
//    public static void saveUserInfo(UserInfoModel userInfoModel) {
//        threadLocal.set(userInfoModel);
//    }
//
//    /**
//     * 获取  UserInfo
//     * @return
//     */
//    public static UserInfoModel getCurrentUser() {
//        return threadLocal.get();
//    }

}
