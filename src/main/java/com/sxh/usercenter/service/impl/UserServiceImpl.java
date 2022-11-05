package com.sxh.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sxh.usercenter.Mapper.UserMapper;
import com.sxh.usercenter.Model.domain.User;
import com.sxh.usercenter.common.ErrorCode;
import com.sxh.usercenter.exception.BusinessException;
import com.sxh.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.sxh.usercenter.constant.userConstant.USER_LOGIN_STATE;

/**
 * @author sxh
 * @description 针对表【user(用户数据)】的数据库操作Service实现
 * @createDate 2022-07-04 23:32:31
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，用于加密密码
     */
    private static final String SALT="songxh";


    @Override
    /*
     * @Description: 更改账户密码
     * @Param: [userAccount, userPassword, checkPassword]
     * @return:
     * @Author: SXH
     * @Date: 2022/7/28
     */
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //基本校验
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword))
            return -1;
        if (userAccount.length()<4)
            return -1;
        if (userPassword.length()<8||checkPassword.length()<8)
            return -1;
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher= Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find())
            return -1;
        if (!userPassword.equals(checkPassword))
            return -1;

        UpdateWrapper<User> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("userAccount",userAccount);
        Long count = userMapper.selectCount(updateWrapper);
        if (count<=0)
            return -1;
        String encryptPassword= DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes(StandardCharsets.UTF_8));

        updateWrapper.set("userPassword",encryptPassword);

        return userMapper.update(null, updateWrapper);
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(userAccount, userPassword))
            return null;
        if (userAccount.length() < 4)
            return null;
        if (userPassword.length() < 8)
            return null;

        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find())
            return null;
        String encryptPassword = getEncryptPassword(userPassword);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user=userMapper.selectOne(queryWrapper);
        if (user == null){

            return null;
        }
        //用户脱敏，主要是不记录密码
        User safeUser=getSafetyUser(user);

        request.getSession().setAttribute(USER_LOGIN_STATE,safeUser);

        return safeUser;
    }
    /**
     * 用户脱敏，即返回不包含密码的用户数据
     * @param user
     * @return
     */
    @Override
    public User getSafetyUser(User user){
        if (user==null)
            return null;
        User safeUser=new User();
        safeUser.setUserId(user.getUserId());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setUserName(user.getUserName());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setGender(user.getGender());
        safeUser.setUserRole(user.getUserRole());
        safeUser.setUserStatus(user.getUserStatus());
        safeUser.setCreateTime(user.getCreateTime());
        safeUser.setUserEmail(user.getUserEmail());
        safeUser.setUserPhone(user.getUserPhone());
        safeUser.setTag(user.getTag());
        safeUser.setUpdateTime(user.getUpdateTime());
        return safeUser;
    }

    @Override
    public String getEncryptPassword(String orginPassword) {
        return DigestUtils.md5DigestAsHex((SALT+orginPassword).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public long setNewUser(String userAccount, String userName, String gender, String userMajor, int userClass) {
        if (StringUtils.isAnyBlank(userAccount,userName,gender,userMajor)) {
            return -1;
        }
        if (userAccount.length()<12)
            return -2;
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find())
            return -3;
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if (count>0){
            log.info("The userAccount has already EXIST!");
            return -4;
        }
        String userPassword=getEncryptPassword("12345678");
        User user=new User();
        user.setUserAccount(userAccount);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setGender(gender);
        user.setAvatarUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
        user.setUserRole(1);
        user.setUserStatus(0);

        boolean save = this.save(user);
        if (!save)
            return -5;
        return user.getUserId();
    }

    @Override
    /**
     * @Description: 用户注销
     * @Param: [request]
     * @return:
     * @Author: SXH
     * @Date: 2022/7/26
     */
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public boolean userDelete(String userAccount) {
        UpdateWrapper<User> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("userAccount",userAccount).set("userStatus",1);
        return this.update(null,updateWrapper);
    }

    @Override
    /**
     * @Description: 更改用户密码
     * @Param: [userAccount, oldPassword, newPassword, checkPassword]
     * @return: -1：旧密码错误 -2：两次密码输入不一致 -3：未成功更新数据 1：成功修改
     * @Author: SXH
     * @Date: 2022/9/4
     */
    public long changeUserPassword(String userAccount, String oldPassword, String newPassword, String checkPassword) {
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        User user=userMapper.selectOne(queryWrapper);
        String encryptOldPassword=getEncryptPassword(oldPassword);
        if (!encryptOldPassword.equals(user.getUserPassword()))
            return -1;
        if (!newPassword.equals(checkPassword))
            return -2;
        UpdateWrapper<User> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("userAccount",userAccount).set("userPassword",getEncryptPassword(newPassword));
        boolean update = this.update(updateWrapper);
        if (!update)
            return -3;
        return 1;

    }

    @Override
    /**
     * @Description:重置用户密码为: 12345678
     * @Param: [userAccount]
     * @return:
     * @Author: SXH
     * @Date: 2022/9/4
     */
    public boolean resetUserPassword(String userAccount) {
        UpdateWrapper<User> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("userAccount",userAccount).set("userPassword",getEncryptPassword("12345678"));
        return this.update(updateWrapper);

    }

//    @Override
    /**
     * @Description: 更新用户数据
     * @Param: [userAccount, userName, gender, userMajor, userClass]
     * @return:
     * @Author: SXH
     * @Date: 2022/9/6
     *
    public long updateUser(String userAccount, String userName, String gender, String userMajor, int userClass) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userAccount",userAccount);
        if (userName != null){
            updateWrapper.set("userName",userName);
        }
        if (gender != null){
            updateWrapper.set("gender",gender);
        }
        if (userMajor != null){
            updateWrapper.set("userMajor",userMajor);
        }
        updateWrapper.set("userClass",userClass);
        boolean update = this.update(updateWrapper);
        if (!update)
            return -1;
        return 1;
    }

    @Override
    /**
     * @Description: 根据用户标签搜索用户
     * @Param: [tagNameList]
     * @return: 用户列表
     * @Author: SXH
     * @Date: 2022/11/1
     */
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        /*
            方法一：通过sql语句进行查询
        */
//        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
//        for(String tagName: tagNameList){
//            queryWrapper.like("tag",tagName);
//        }
//        List<User> list=userMapper.selectList(queryWrapper);
//        return list.stream().map(this::getSafetyUser).collect(Collectors.toList());
        /*
            方法二：先查询所有的用户，再将用户信息转换为json，对json信息进行匹配
        * */
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        List<User> userList=userMapper.selectList(queryWrapper);
        Gson gson=new Gson();
        return userList.stream().filter(user -> {
           String tagStr=user.getTag();
           if (StringUtils.isBlank(tagStr))
               return false;
            Set<String> tagNameSet=gson.fromJson(tagStr,new TypeToken<Set<String>>(){}.getType());
            tagNameSet= Optional.ofNullable(tagNameSet).orElse(new HashSet<>());
            for (String tagName: tagNameList){
                if (!tagNameSet.contains(tagName))
                    return false;
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }
}