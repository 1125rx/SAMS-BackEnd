package com.sxh.usercenter.Model.request.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-09-04 12:19
 **/
@Data
public class ChangeUserPasswordRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 6477846384347501560L;

    private String oldPassword;
    private String newPassword;
    private String checkPassword;
}
