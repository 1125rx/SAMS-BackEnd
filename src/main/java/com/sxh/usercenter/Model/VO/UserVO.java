package com.sxh.usercenter.Model.VO;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户
 * @TableName user
 */
@Data
public class UserVO implements Serializable {
    /**
     * id
     */
    private long userId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private String gender;
    /**
     * 电话
     */
    private String userPhone;
    /**
     * 邮箱
     */
    private String userEmail;

    /**
     * 状态 0 - 正常
     */
    private Integer userStatus;

    /**
     * 用户地址
     */
    private String userLocation;

    /**
     * 用户描述
     */
    private String userDescription;

    /**
     *
     */
    private String userSchool;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    private Integer userRole;

    /**
     * 星球编号
     */
    private String planetCode;

    /**
     * 标签json列表
     */
    private List<String> tag;

    /**
     * 用户年龄
     */
    private Integer userAge;

    private static final long serialVersionUID = 1L;
}