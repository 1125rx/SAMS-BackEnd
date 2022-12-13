package com.sxh.usercenter.Model.request.team;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-12-01 20:52
 **/
@Data
public class TeamApplyDealRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -2209954573597431002L;

    private long id;
}
