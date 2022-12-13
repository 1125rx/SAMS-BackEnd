package com.sxh.usercenter.Model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 队伍
 * @TableName team
 */
@TableName(value ="team")
@Data
public class Team implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long t_id;

    /**
     * 队伍名称
     */
    private String t_name;

    /**
     * 描述
     */
    private String t_description;

    /**
     * 最大人数
     */
    private Integer t_maxNum;

    /**
     * 当前人数
     */
    private Integer t_num;

    /**
     * 过期时间
     */
    private Date t_expireTime;

    /**
     * 用户id（队长 id）
     */
    private Long t_userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer t_status;

    /**
     * 密码
     */
    private String t_password;

    /**
     * 创建时间
     */
    private Date t_createTime;

    /**
     * 
     */
    private Date t_updateTime;

    /**
     * 是否删除
     */
    private Integer t_isDelete;

    /**
     * 队伍头像
     */
    private String t_avatarUrl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}