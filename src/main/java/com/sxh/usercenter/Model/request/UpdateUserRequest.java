package com.sxh.usercenter.Model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-09-16 09:02
 **/
@Data
public class UpdateUserRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 6920354526230759127L;

    private String userAccount;
    private String userName;
    private String gender;
    private String userMajor;
    private int userClass;
}
