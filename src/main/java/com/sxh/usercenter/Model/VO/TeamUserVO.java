package com.sxh.usercenter.Model.VO;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-11-28 11:26
 **/
@Data
public class TeamUserVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6754710164978291279L;

    /**
     * id
     */
    private Long t_id;

    /**
     * 队伍名称
     */
    private String t_name;

    /**
     * 队伍头像
     */
    private String t_avatarUrl;

    /**
     * 描述
     */
    private String t_description;

    /**
     * 最大人数
     */
    private Integer t_maxNum;

    /**
     * 过期时间
     */
    private Date t_expireTime;

    /**
     * 用户id
     */
    private Long t_userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer t_status;

    /**
     * 创建时间
     */
    private Date t_createTime;

    /**
     * 更新时间
     */
    private Date t_updateTime;

    /**
     * 创建人用户信息
     */
    private UserVO t_createUser;

    private List<UserVO> members;

    /**
     * 已加入的用户数
     */
    private long hasJoinNum;

    /**
     * 是否已加入队伍
     */
    private boolean hasJoin = false;

}
