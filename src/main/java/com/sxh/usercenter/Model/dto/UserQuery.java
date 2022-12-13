package com.sxh.usercenter.Model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-12-13 00:23
 **/
@Data
public class UserQuery implements Serializable {
    @Serial
    private static final long serialVersionUID = 371477080040161028L;

    private String userName;
    private String gender;
    private String userAccount;
}
