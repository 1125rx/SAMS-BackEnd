package com.sxh.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sxh.usercenter.Mapper.UserMapper;
import com.sxh.usercenter.Model.domain.User;
import com.sxh.usercenter.Model.request.ChangeUserPasswordRequest;
import com.sxh.usercenter.Model.request.UpdateUserRequest;
import com.sxh.usercenter.Model.request.UserLoginRequest;
import com.sxh.usercenter.Model.request.UserRegisterRequest;
import com.sxh.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sxh.usercenter.constant.userConstant.USER_LOGIN_STATE;

/**
 * @program: usercenter
 * @description:用户接口
 * @author: SXH
 * @create: 2022-06-25 09:50
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    @PostMapping("/register")
    /*
    * @Description: 注册接口
    * @Param: [userRegisterRequest, request]
    * @return:
    * @Author: SXH
    * @Date: 2022/7/28
    */
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest, HttpServletRequest request){
        if (userRegisterRequest==null)
            return null;
        Object userObj=request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser=(User) userObj;
        if (currentUser==null)
            return null;
        String userAccount=currentUser.getUserAccount();
        String userPassword=userRegisterRequest.getUserPassword();
        String checkPassword=userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword))
            return null;
        return userService.userRegister(userAccount, userPassword, checkPassword);
    }
    @PostMapping("/changePassword")
    /*
    * @Description: 更改用户密码接口
    * @Param: [changeUserPasswordRequest, request]
    * @return:
    * @Author: SXH
    * @Date: 2022/9/4
    */
    public long changePassword(@RequestBody ChangeUserPasswordRequest changeUserPasswordRequest, HttpServletRequest request){
        if (changeUserPasswordRequest == null)
            return -4;
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null)
            return -5;
        String userAccount=currentUser.getUserAccount();
        String oldPassword = changeUserPasswordRequest.getOldPassword();
        String newPassword = changeUserPasswordRequest.getNewPassword();
        String checkPassword = changeUserPasswordRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount,oldPassword,newPassword,checkPassword))
            return -6;
        return userService.changeUserPassword(userAccount,oldPassword,newPassword,checkPassword);
    }


    @PostMapping("/login")
    /*
    * @Description: 登录接口
    * @Param: [userLoginRequest, request]
    * @return: User
    * @Author: SXH
    * @Date: 2022/11/1
    */
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest==null)
            return null;
        String userAccount=userLoginRequest.getUserAccount();
        String userPassword=userLoginRequest.getUserPassword();
        String type=userLoginRequest.getType();
        if (StringUtils.isAnyBlank(userAccount,userPassword))
            return null;
        if (type.equals("account"))
            return userService.userLogin(userAccount,userPassword,request);
        else
        {
            QueryWrapper<User> queryWrapper=new QueryWrapper<>();
            User user=userMapper.selectOne(queryWrapper.eq("email",userAccount));
            return userService.userLogin(user.getUserAccount(),userPassword,request);
        }
    }

    //查询用户接口
    @GetMapping("/search")
    public List<User> searchUser(String userAccount,String userName, HttpServletRequest request){
        if (!isTeacher(request))
            return new ArrayList<>();
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        if (StringUtils.isNotBlank(userAccount))
            queryWrapper.like("userAccount",userAccount);
        if (StringUtils.isNotBlank(userName))
            queryWrapper.like("userName",userName);
        queryWrapper.eq("userRole",1);
        List<User> userList=userService.list(queryWrapper);
        return userList.stream().map(resUser -> userService.getSafetyUser(resUser)).collect(Collectors.toList());
    }

    //删除用户接口
    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody String userAccount,HttpServletRequest request){
        if (!isTeacher(request))
            return false;
        return userService.userDelete(userAccount);
    }   

    @PostMapping("/resetPassword")
    /*
    * @Description: 重置用户密码
    * @Param: [userAccount, request]
    * @return:
    * @Author: SXH
    * @Date: 2022/9/4
    */
    public boolean resetPassword(@RequestBody String userAccount, HttpServletRequest request){
        if (!isTeacher(request))
            return false;
        return userService.resetUserPassword(userAccount);
    }

    @GetMapping("/current")
    /*
     @Description: 获取当前登录用户信息
    * @Param: [request]
    * @return:
    * @Author: SXH
    * @Date: 2022/7/28
    */
    public User getCurrentUser(HttpServletRequest request){
        Object userObj=request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser=(User) userObj;
        if (currentUser==null)
            return null;
        long userId=currentUser.getUserId();
        User user=userService.getById(userId);
        return userService.getSafetyUser(user);
    }

    /**
    * @Description: 检测请求用户是否为教师账户
    * @Param:
    * @return:
    * @Author: SXH
    * @Date: 2022/9/4
    */

    public boolean isTeacher(HttpServletRequest request){
        Object userObj=request.getSession().getAttribute(USER_LOGIN_STATE);
        User user=(User)userObj;
        return user != null && user.getUserRole() != 1;
    }

////    @PostMapping("/addUser")
////    /**
//    * @Description: 添加新的用户信息
//    * @Param: [request] 添加新用户请求体
//    * @return:
//    * @Author: SXH
//    * @Date: 2022/7/28
//    */
//    public Long createUser(@RequestBody SetNewUserRequest request, HttpServletRequest httpServletRequest){
//        if (request==null)
//            return null;
//        String userAccount=request.getUserAccount();
//        String userName=request.getUserName();
//        String gender=request.getGender();
//        int userClass=request.getUserClass();
//        Object userObj=httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
//        User user=(User)userObj;
//        String userMajor = user.getUserMajor();
//        if (StringUtils.isAnyBlank(userAccount,userName,userMajor,gender))
//            return null;
//        return userService.setNewUser(userAccount, userName, gender, userMajor, userClass);
//    }

    @PostMapping("/logout")
    /*
    * @Description: 退出当前登录接口
    * @Param: [request]
    * @return:
    * @Author: SXH
    * @Date: 2022/7/28
    */
    public Integer userLogout(HttpServletRequest request){
        if (request==null)
            return null;
        return userService.userLogout(request);

    }
    @PostMapping("/updateUser")
    /*
    * @Description: 更新用户数据
    * @Param: [updateUserRequest]
    * @return:
    * @Author: SXH
    * @Date: 2022/9/16
    */
    public long updateUserController(@RequestBody UpdateUserRequest updateUserRequest){
        String userAccount = updateUserRequest.getUserAccount();
        String userName = updateUserRequest.getUserName();
        String gender = updateUserRequest.getGender();
        String userMajor = updateUserRequest.getUserMajor();
        int userClass = updateUserRequest.getUserClass();
        return userService.updateUser(userAccount,userName,gender,userMajor,userClass);
    }
}
