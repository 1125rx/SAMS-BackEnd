package com.sxh.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sxh.usercenter.Mapper.UserMapper;
import com.sxh.usercenter.Model.VO.UserVO;
import com.sxh.usercenter.Model.domain.User;
import com.sxh.usercenter.Model.dto.UserQuery;
import com.sxh.usercenter.Model.request.user.*;
import com.sxh.usercenter.common.BaseResponse;
import com.sxh.usercenter.common.ErrorCode;
import com.sxh.usercenter.common.ResultUtils;
import com.sxh.usercenter.exception.BusinessException;
import com.sxh.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

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
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest, HttpServletRequest request){
        if (userRegisterRequest==null)
            return null;
        String userAccount=userRegisterRequest.getUserAccount();
        String userPassword=userRegisterRequest.getUserPassword();
        String checkPassword=userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword))
            return null;
        long re = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(re);
    }
    @PostMapping("/changePassword")
    /*
    * @Description: 更改用户密码接口
    * @Param: [changeUserPasswordRequest, request]
    * @return:
    * @Author: SXH
    * @Date: 2022/9/4
    */
    public BaseResponse<Long> changePassword(@RequestBody ChangeUserPasswordRequest changeUserPasswordRequest, HttpServletRequest request){
        if (changeUserPasswordRequest == null)
           throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        User currentUser=userService.getLoginUser(request);
        if (currentUser == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"获取用户信息失败");
        String userAccount=currentUser.getUserAccount();
        String oldPassword = changeUserPasswordRequest.getOldPassword();
        String newPassword = changeUserPasswordRequest.getNewPassword();
        String checkPassword = changeUserPasswordRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount,oldPassword,newPassword,checkPassword))
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        long l = userService.changeUserPassword(userAccount, oldPassword, newPassword, checkPassword);
        return ResultUtils.success(l);
    }


    @PostMapping("/login")
    /*
    * @Description: 登录接口
    * @Param: [userLoginRequest, request]
    * @return: User
    * @Author: SXH
    * @Date: 2022/11/1
    */
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest==null)
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        String userAccount=userLoginRequest.getUserAccount();
        String userPassword=userLoginRequest.getUserPassword();
        String type=userLoginRequest.getType();
        if (StringUtils.isAnyBlank(userAccount,userPassword))
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        if (type.equals("account")){
            User user=userService.userLogin(userAccount,userPassword,request);
            if(user == null)
                return ResultUtils.error(ErrorCode.PARAMS_ERROR,"错误的账号或密码");
            else
                return ResultUtils.success(user);
        }
        else
        {
            QueryWrapper<User> queryWrapper=new QueryWrapper<>();
            User user=userMapper.selectOne(queryWrapper.eq("email",userAccount));
            User result=userService.userLogin(user.getUserAccount(),userPassword,request);
            return ResultUtils.success(result);
        }
    }

    //查询用户接口
    @PostMapping("/search")
    public BaseResponse<List<UserVO>> searchUser(@RequestBody UserQuery userQuery, HttpServletRequest request){
        if (!isTeacher(request))
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        if (userQuery==null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        String userAccount=userQuery.getUserAccount();
        String userName=userQuery.getUserName();
        String gender=userQuery.getGender();
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        if (StringUtils.isNotBlank(userAccount))
            queryWrapper.like("userAccount",userAccount);
        if (StringUtils.isNotBlank(userName))
            queryWrapper.like("userName",userName);
        if (StringUtils.isNotBlank(gender))
            queryWrapper.eq("gender",gender);
        queryWrapper.eq("userRole",0);
        List<User> userList=userService.list(queryWrapper);
        List<UserVO> list = userList.stream().map(resUser -> userService.getUserVO(resUser)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    //删除用户接口
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody String userAccount,HttpServletRequest request){
        if (!isTeacher(request))
            throw new BusinessException(ErrorCode.NO_AUTH);
        boolean delete = userService.userDelete(userAccount);
        return ResultUtils.success(delete);
    }   

    @PostMapping("/resetPassword")
    /*
    * @Description: 重置用户密码
    * @Param: [userAccount, request]
    * @return:
    * @Author: SXH
    * @Date: 2022/9/4
    */
    public BaseResponse<Boolean> resetPassword(@RequestBody String userAccount, HttpServletRequest request){
        if (!isTeacher(request))
            throw new BusinessException(ErrorCode.NO_AUTH);
        boolean b = userService.resetUserPassword(userAccount);
        return ResultUtils.success(b);
    }

    @GetMapping("/current")
    /*
     @Description: 获取当前登录用户信息
    * @Param: [request]
    * @return:
    * @Author: SXH
    * @Date: 2022/7/28
    */
    public BaseResponse<UserVO> getCurrentUser(HttpServletRequest request){
        User currentUser=userService.getLoginUser(request);
        if (currentUser==null)
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        long userId=currentUser.getUserId();
        User user=userService.getById(userId);
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
    * @Description: 检测请求用户是否为教师账户
    * @Param:
    * @return:
    * @Author: SXH
    * @Date: 2022/9/4
    */

    public boolean isTeacher(HttpServletRequest request){
        User user=userService.getLoginUser(request);
        return user != null && user.getUserRole() != 1;
    }
    @PostMapping("/logout")
    /*
    * @Description: 退出当前登录接口
    * @Param: [request]
    * @return:
    * @Author: SXH
    * @Date: 2022/7/28
    */
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if (request==null)
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        int i = userService.userLogout(request);
        return ResultUtils.success(i);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> userUpdate(@RequestBody UpdateUserRequest updateUserRequest, HttpServletRequest request){
        if (updateUserRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        User loginUser=userService.getLoginUser(request);
        int updateUser = userService.updateUser(updateUserRequest, loginUser);
        return ResultUtils.success(updateUser);
    }
    @GetMapping("/getById")
    public BaseResponse<User> getUserByUserId(int userId){
        User user=userService.getById(userId);
        if (user == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"未检索到用户数据");
        return ResultUtils.success(user);
    }
    @PostMapping("/match")
    public BaseResponse<List<UserVO>> matchUsers(@RequestBody MatchUserRequest matchUserRequest, HttpServletRequest request){
        if (matchUserRequest==null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        long num=matchUserRequest.getNum();
        if (num <= 0 || num > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        List<UserVO> userVOS = userService.matchUsers(num, user);
        return ResultUtils.success(userVOS);
    }

    @PostMapping("/tags")
    public BaseResponse<Boolean> updateTags(@RequestBody UpdateTagRequest updateTagRequest,HttpServletRequest request){
        if (request==null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        if (updateTagRequest==null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        String tags=updateTagRequest.getTags();
        loginUser.setTag(tags);
        boolean update = userService.updateById(loginUser);
        return ResultUtils.success(update);
    }

//    public List<String> getUserTags(HttpServletRequest request){
//        User user=userService.getLoginUser(request);
//        if (user == null)
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"未检索到用户数据");
//        String tag=user.getTag();
//
//    }
}
