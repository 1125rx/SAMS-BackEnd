package com.sxh.usercenter.Model.VO;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-12-15 18:53
 **/
@Data
public class ArticleVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -3311286268806738051L;

    private Integer id;

    /**
     * 发布人id
     */
    private UserVO userVO;

    /**
     * 队伍Id
     */
    private Integer teamId;

    /**
     * 正文部分
     */
    private String mainBody;

    /**
     *
     */
    private Integer likeNum;

    /**
     * 发布时间
     */
    private Date publishTime;
}
