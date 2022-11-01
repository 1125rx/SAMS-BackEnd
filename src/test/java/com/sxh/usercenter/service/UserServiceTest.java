package com.sxh.usercenter.service;

import com.sxh.usercenter.Model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void testSearchUsersByTags(){
        List<String> tagNames= Arrays.asList("Java","Python");
        List<User> userList=userService.searchUsersByTags(tagNames);
        System.out.println(userList);
        Assert.assertNotNull(userList);
    }

}