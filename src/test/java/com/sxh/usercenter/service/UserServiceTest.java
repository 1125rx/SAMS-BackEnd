package com.sxh.usercenter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sxh.usercenter.Mapper.UserMapper;
import com.sxh.usercenter.Model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    @Test
    void testSearchUsersByTags(){
        List<String> tagNames= Arrays.asList("Java","Python");
        List<User> userList=userService.searchUsersByTags(tagNames);
        System.out.println(userList);
        Assert.assertNotNull(userList);
    }

    @Test
    void testGetUsersByTags(){
        User user=userMapper.selectById(2);
        String tags=user.getTag().substring(1,user.getTag().length()-1);
        String[] list=tags.split(",");
        List<String> re=new ArrayList<>();
        for (String tag: list){
            re.add(tag.substring(1,tag.length()-1));
        }
        for (String r:re){
            System.out.println(r);
        }
    }

}