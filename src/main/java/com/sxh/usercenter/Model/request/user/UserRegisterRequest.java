package com.sxh.usercenter.Model.request.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-06-25 10:01
 **/
@Data
public class UserRegisterRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1521362975129650787L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
