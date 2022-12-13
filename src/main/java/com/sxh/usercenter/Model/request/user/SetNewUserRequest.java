package com.sxh.usercenter.Model.request.user;

import lombok.Data;

import java.io.Serial;

import java.io.Serializable;

/**
 * @program: usercenter
 * @description: Request for add a new user
 * @author: SXH
 * @create: 2022-07-01 12:47
 **/
@Data
public class SetNewUserRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -2466923169607758527L;
    private String userAccount;
    private String userName;
    private String gender;
    private int userClass;

}
