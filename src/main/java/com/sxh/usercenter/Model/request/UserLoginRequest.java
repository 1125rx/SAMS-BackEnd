package com.sxh.usercenter.Model.request;

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
public class UserLoginRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 323066308972153445L;

    private String userAccount;
    private String userPassword;
    private String type;
}
