package com.sxh.usercenter.Model.request.article;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-12-15 18:48
 **/
@Data
public class ArticleGetRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -4161642907983084072L;

    private long teamId;
}
