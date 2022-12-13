package com.sxh.usercenter.Model.request.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-12-13 12:01
 **/
@Data
public class MatchUserRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 6581627454351938230L;

    private long num;
}
