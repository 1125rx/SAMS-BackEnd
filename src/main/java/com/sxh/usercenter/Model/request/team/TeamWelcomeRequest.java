package com.sxh.usercenter.Model.request.team;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-12-14 09:42
 **/
@Data
public class TeamWelcomeRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 4734590276496560573L;

    private long userId;
    private long teamId;
    private String description;
}
