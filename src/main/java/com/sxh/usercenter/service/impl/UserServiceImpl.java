package com.sxh.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sxh.usercenter.Mapper.UserMapper;
import com.sxh.usercenter.Model.VO.UserVO;
import com.sxh.usercenter.Model.domain.User;
import com.sxh.usercenter.Model.request.user.UpdateUserRequest;
import com.sxh.usercenter.common.ErrorCode;
import com.sxh.usercenter.exception.BusinessException;
import com.sxh.usercenter.service.UserService;
import com.sxh.usercenter.utils.AlgorithmUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.sxh.usercenter.constant.userConstant.ADMIN_ROLE;
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        if (userAccount.length()<4)
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度过短");
        if (userPassword.length()<8||checkPassword.length()<8)
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度过短");
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher= Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find())
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不符合规范，请重新尝试");
        if (!userPassword.equals(checkPassword))
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码输入不一致，请重新尝试");

        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count=userMapper.selectCount(queryWrapper);
        if (count > 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号重复，请重新尝试");
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserName("UnKnown");
        user.setAvatarUrl("https://i.postimg.cc/Gm2fnw9T/Konachan-com-344684-sample.jpg");
        user.setUserPassword(encryptPassword);boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"注册失败");
        }
        return user.getUserId();
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
            log.info("UserAccount or UserPassword may be wrong.");
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
        safeUser.setUserAge(user.getUserAge());
        safeUser.setUserDescription(user.getUserDescription());
        safeUser.setUserLocation(user.getUserLocation());
        safeUser.setUserSchool(user.getUserSchool());
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"输入密码错误");
        if (!newPassword.equals(checkPassword))
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码输入不一致");
        UpdateWrapper<User> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("userAccount",userAccount).set("userPassword",getEncryptPassword(newPassword));
        boolean update = this.update(updateWrapper);
        if (!update)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"未成功更新数据");
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

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null)
            return null;
        Object obj=request.getSession().getAttribute(USER_LOGIN_STATE);
        User loginUser=(User) obj;
        if (loginUser == null)
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        return loginUser;
    }

    @Override
    public int updateUser(UpdateUserRequest updateUserRequest, User loginUser) {
        //如果是管理员，可以更改任意用户
        //如果不是管理员，只能修改自己的账户
        if (updateUserRequest== null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        long userId= loginUser.getUserId();
        User oldUser=userMapper.selectById(userId);
        if (updateUserRequest.getUserAccount()!=null)
            if (testUserAccount(updateUserRequest.getUserAccount()))
                oldUser.setUserAccount(updateUserRequest.getUserAccount());
        if (updateUserRequest.getUserName()!=null)
            oldUser.setUserName(updateUserRequest.getUserName());
        if (updateUserRequest.getUserPhone()!=null)
            oldUser.setUserPhone(updateUserRequest.getUserPhone());
        if (updateUserRequest.getUserDescription()!=null)
            oldUser.setUserDescription(updateUserRequest.getUserDescription());
        if (updateUserRequest.getUserSchool()!=null)
            oldUser.setUserSchool(updateUserRequest.getUserSchool());
        if (updateUserRequest.getUserLocation()!=null)
            oldUser.setUserLocation(updateUserRequest.getUserLocation());
        if (updateUserRequest.getUserAge()!=null)
            oldUser.setUserAge(updateUserRequest.getUserAge());
        return userMapper.updateById(oldUser);
    }

    @Override
    public UserVO getUserVO(User user) {
        UserVO userVO = new UserVO();
        userVO.setUserId(user.getUserId());
        userVO.setUserName(user.getUserName());
        userVO.setUserAccount(user.getUserAccount());
        userVO.setAvatarUrl(user.getAvatarUrl());
        userVO.setGender(user.getGender());
        userVO.setUserPhone(user.getUserPhone());
        userVO.setUserEmail(user.getUserEmail());
        userVO.setUserStatus(user.getUserStatus());
        userVO.setUserDescription(user.getUserDescription());
        userVO.setUserSchool(user.getUserSchool());
        userVO.setUserLocation(user.getUserLocation());
        userVO.setCreateTime(user.getCreateTime());
        userVO.setUpdateTime(user.getUpdateTime());
        userVO.setIsDelete(user.getIsDelete());
        userVO.setUserRole(user.getUserRole());
        if (user.getTag()!=null)
            userVO.setTag(getTagsList(user.getTag()));
        else userVO.setTag(new ArrayList<>());
        userVO.setUserAge(user.getUserAge());
        return userVO;
    }

    @Override
    public List<String> getTagsList(String tagNameList) {
        String tags=tagNameList.substring(1,tagNameList.length()-1);
        String[] tag=tags.split(",");
        List<String> tagsList=new ArrayList<String>();
        for (String t:tag) {
            tagsList.add(t.substring(1,t.length()-1));
        }
        return tagsList;
    }

    private boolean testUserAccount(String userAccount) {
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        if (userMapper.exists(queryWrapper))
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已存在");
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher= Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find())
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不符合规范，请重新尝试");
        return true;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;

    }

    @Override
    public List<UserVO> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("userId", "tag");
        queryWrapper.isNotNull("tag");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTag();
        List<String> tagList = this.getTagsList(tags);
        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (User user : userList) {
            String userTags = user.getTag();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getUserId() == loginUser.getUserId()) {
                continue;
            }
            List<String> userTagList = this.getTagsList(userTags);
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .toList();
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getUserId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("userId", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .map(this::getSafetyUser)
                .collect(Collectors.groupingBy(User::getUserId));
        List<UserVO> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(this.getUserVO(userIdUserListMap.get(userId).get(0)));
        }
        return finalUserList;
    }
}