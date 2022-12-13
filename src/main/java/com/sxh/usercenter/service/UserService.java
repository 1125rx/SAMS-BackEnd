package com.sxh.usercenter.service;

import com.sxh.usercenter.Model.VO.UserVO;
import com.sxh.usercenter.Model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sxh.usercenter.Model.request.user.UpdateUserRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author sxh
* @description 针对表【user(用户数据)】的数据库操作Service
* @createDate 2022-07-04 23:32:31
*/
public interface UserService extends IService<User> {

    //用户注册
    long userRegister(String userAccount,String userPassword,String checkPassword);
    //用户登录
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);
    //用户脱敏
    User getSafetyUser(User user);
    //密码加密
    String getEncryptPassword(String orginPassword);
    //添加新用户
    long setNewUser(String userAccount,String userName,String gender,String userMajor,int userClass);
    //用户登出
    int userLogout(HttpServletRequest request);
    //删除用户
    boolean userDelete(String userAccount);
    //更改密码
    long changeUserPassword(String userAccount, String oldPassword, String newPassword, String checkPassword);
    //重置用户密码
    boolean resetUserPassword(String userAccount);
    //根据标签搜索用户
    List<User> searchUsersByTags(List<String> tagNameList);

    User getLoginUser(HttpServletRequest request);

    int updateUser(UpdateUserRequest updateUserRequest, User loginUser);

    UserVO getUserVO(User user);

    List<String> getTagsList(String tagNameList);

    boolean isAdmin(HttpServletRequest request);

    List<UserVO> matchUsers(long num,User loginUser);

}
