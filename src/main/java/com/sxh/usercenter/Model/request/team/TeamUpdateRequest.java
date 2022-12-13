package com.sxh.usercenter.Model.request.team;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-11-28 17:55
 **/
@Data
public class TeamUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -5489196202268959077L;

    /**
     * id
     */
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

    /**
     * 队伍头像
     */
    private String t_avatarUrl;


}
