package com.sxh.usercenter.Model.request.article;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-12-16 10:14
 **/
@Data
public class ArticleDeleteRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 166350988208996958L;

    private long teamId;
    private long id;
}
