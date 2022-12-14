package com.sxh.usercenter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sxh.usercenter.Mapper.UserMapper;
import com.sxh.usercenter.Model.domain.User;
import com.sxh.usercenter.utils.AlgorithmUtils;
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
    void testMatch(){
        List<String> tags1=Arrays.asList("C");
        List<String> tags2=Arrays.asList("Java","C");
        List<String> tags3=Arrays.asList("Java","C++","Python");
        List<String> tags4=Arrays.asList("C++");
        System.out.println(AlgorithmUtils.minDistance(tags1,tags2));
        System.out.println(AlgorithmUtils.minDistance(tags1,tags3));
        System.out.println(AlgorithmUtils.minDistance(tags1,tags4));
        System.out.println(AlgorithmUtils.minDistance(tags2,tags3));
        System.out.println(AlgorithmUtils.matchTags(tags1,tags2));
        System.out.println(AlgorithmUtils.matchTags(tags1,tags3));
        System.out.println(AlgorithmUtils.matchTags(tags1,tags4));
        System.out.println(AlgorithmUtils.matchTags(tags2,tags3));
        System.out.println(AlgorithmUtils.matchTags(tags2,tags4));
        System.out.println(AlgorithmUtils.minDistance("c","c++"));
        System.out.println(AlgorithmUtils.minDistance("c","java"));

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