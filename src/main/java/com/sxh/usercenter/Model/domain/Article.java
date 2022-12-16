package com.sxh.usercenter.Model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 发布文章表
 * @TableName article
 */
@TableName(value ="article")
@Data
public class Article implements Serializable {
    /**
     * 文章id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 发布人id
     */
    private long userId;

    /**
     * 队伍Id
     */
    private long teamId;

    /**
     * 正文部分
     */
    private String mainBody;

    /**
     * 
     */
    private Integer likeNum;

    /**
     * 发布时间
     */
    private Date publishTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}