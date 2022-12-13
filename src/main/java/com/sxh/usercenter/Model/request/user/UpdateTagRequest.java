package com.sxh.usercenter.Model.request.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-12-13 21:12
 **/
@Data
public class UpdateTagRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -7850253286890896612L;

    private String tags;
}
