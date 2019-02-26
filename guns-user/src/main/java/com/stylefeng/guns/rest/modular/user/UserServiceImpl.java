package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;
import com.stylefeng.guns.core.util.MD5Util;
import com.stylefeng.guns.rest.common.persistence.dao.MoocUserTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocUserT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
@Service(interfaceClass = UserAPI.class, loadbalance ="roundrobin" )
public class UserServiceImpl implements UserAPI {


    /**
     * 忽略这里的错误
     */
    @Autowired
    private MoocUserTMapper moocUserTMapper;


    /**
     *  注册功能的实现
     * @param userModel 保存部分用户信息的类
     * @return true or false
     */
    @Override
    public boolean register(UserModel userModel) {

        // 1. 获取注册信息：已经获得，就是传入的参数

        // 2. 将注册信息实体转换为数据实体
        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUserName(userModel.getUsername());

        // 2.1 特别注意密码的转换，明文密码必须转换为加密后的密码
        // 实际开发中：MD5 混淆加密 + 盐值（或者使用 shiro 框架）， 我们在此次项目中直接使用 MD5Util 这个工具类
        String md5PWD = MD5Util.encrypt(userModel.getPassword());
        moocUserT.setUserPwd(md5PWD);

        moocUserT.setAddress(userModel.getAddress());
        moocUserT.setEmail(userModel.getEmail());
        moocUserT.setUserPhone(userModel.getPhone());
        // 3. 将数据实体存入数据库
        Integer insert = moocUserTMapper.insert(moocUserT);
        if (insert > 0) {
            return true;
        }else {
            return false;
        }

    }


    /**
     * 登陆功能的实现
     * @param username 前端传入的账号
     * @param password 前端传入的密码
     * @return true:用户id, false: 0
     */
    @Override
    public int login(String username, String password) {

        // 根据登陆账号获取数据库信息

        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUserName(username);

        MoocUserT result = moocUserTMapper.selectOne(moocUserT);
        // 将获取到的结果，和加密后的密码做比对

        if (result != null && result.getUuid() > 0) {
            String dbPwd = result.getUserPwd();
            String userPwd = MD5Util.encrypt(password);
            if (userPwd.equals(dbPwd)) {

                // 返回用户 id
                return result.getUuid();
            }
        }
        return 0;
    }



    @Override
    public boolean checkUserName(String username) {

        EntityWrapper<MoocUserT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("user_name", username);
        Integer result = moocUserTMapper.selectCount(entityWrapper);
        if (result != null && result > 0) {
            // 当存在该 username 时，返回 flase
            return false;
        }else {
            // 不存在时，返回 true
            return true;
        }
    }


    private UserInfoModel do2UserInfo(MoocUserT moocUserT) {
        UserInfoModel userInfoModel = new UserInfoModel();
        // 添加属性
        userInfoModel.setUuid(moocUserT.getUuid());
        userInfoModel.setUsername(moocUserT.getUserName());
        userInfoModel.setSex(moocUserT.getUserSex());
        userInfoModel.setPhone(moocUserT.getUserPhone());
        userInfoModel.setNickname(moocUserT.getNickName());
        userInfoModel.setLifeState(moocUserT.getLifeState());
        userInfoModel.setHeadAddress(moocUserT.getHeadUrl());
        userInfoModel.setEmail(moocUserT.getEmail());
        userInfoModel.setBirthday(moocUserT.getBirthday());
        userInfoModel.setBiography(moocUserT.getBiography());
        userInfoModel.setAddress(moocUserT.getAddress());
        // 注意这里需要添加 getTime()，来转换格式
//        userInfoModel.setUpdateTime(moocUserT.getUpdateTime().getTime());
        userInfoModel.setCreateTime(moocUserT.getBeginTime());
        return userInfoModel;
    }

    /**
     * 查询用户信息
     * @param uuid 唯一 id
     * @return UserInfoModel
     */
    @Override
    public UserInfoModel getUserInfo(int uuid) {

        // 1. 根据主键（uuid） 查询用户信息， uuid -> MoocUserT
        MoocUserT moocUserT = moocUserTMapper.selectById(uuid);
        // 2. 将 MoocUserT 转换为 UserInfoModel
        UserInfoModel  userInfoModel = do2UserInfo(moocUserT);
        // 3. 返回 UserInfoModel
        return userInfoModel;
    }


    private Date time2Date(long time) {
        Date date = new Date(time);
        return date;
    }
    @Override
    public UserInfoModel updateUserInfo(UserInfoModel userInfoModel) {

        // 1. 将传入的数据转换为 MoocUserT
        // 1. 默认账号密码不能被修改

        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUuid(userInfoModel.getUuid());
        moocUserT.setUserSex(userInfoModel.getSex());
        moocUserT.setUpdateTime(time2Date(System.currentTimeMillis()));
        moocUserT.setNickName(userInfoModel.getNickname());
        moocUserT.setLifeState(userInfoModel.getLifeState());
        moocUserT.setHeadUrl(userInfoModel.getHeadAddress());
        moocUserT.setBirthday(userInfoModel.getBirthday());
        moocUserT.setBiography(userInfoModel.getBiography());
        moocUserT.setBeginTime(userInfoModel.getCreateTime());
        moocUserT.setAddress(userInfoModel.getAddress());
        moocUserT.setEmail(userInfoModel.getEmail());
        moocUserT.setUserPhone(userInfoModel.getPhone());

        // 2. 将数据数据存入数据库
        Integer isSuccess = moocUserTMapper.updateById(moocUserT);
        if (isSuccess > 0) {

            // 3. 根据 ID  将用户信息查询出来
            UserInfoModel updatedUserInfo = getUserInfo(moocUserT.getUuid());
            // 4. 将更新后用户信息返回给前端 （通过这个过程来保证前端数据和数据库的数据一致）
            return updatedUserInfo;


        }else {
            // 没有成功，则将原来的内容返回
            return userInfoModel;
        }


    }
}
