package com.sxh.usercenter.Model.request.team;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-11-28 17:58
 **/
@Data
public class TeamJoinRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -1742199217970905562L;

    /**
     * id
     */
    private Long t_id;

    /**
     * 密码
     */
//    private String t_password;

    private String details;

}
