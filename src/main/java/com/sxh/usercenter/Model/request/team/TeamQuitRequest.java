package com.sxh.usercenter.Model.request.team;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-11-28 18:00
 **/
@Data
public class TeamQuitRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -6387193275391720656L;
    /**
     * id
     */
    private long t_id;

}
