package com.sxh.usercenter.Model.request.article;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-12-16 09:19
 **/
@Data
public class ArticlePublishRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 2028594089062337379L;

    private long teamId;
    private String mainBody;
}
