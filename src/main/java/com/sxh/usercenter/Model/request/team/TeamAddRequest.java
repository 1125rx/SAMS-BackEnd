package com.sxh.usercenter.Model.request.team;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-11-28 17:57
 **/
@Data
public class TeamAddRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -4052678893626720061L;

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
     * 过期时间
     */
    private Date t_expireTime;
    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer t_status;

    /**
     * 密码
     */
    private String t_password;


}
