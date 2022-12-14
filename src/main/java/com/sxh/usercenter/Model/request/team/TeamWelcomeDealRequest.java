package com.sxh.usercenter.Model.request.team;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-12-14 11:04
 **/
@Data
public class TeamWelcomeDealRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 6212769128376489461L;

    private long id;
}
