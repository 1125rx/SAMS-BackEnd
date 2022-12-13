package com.sxh.usercenter.Model.VO;

import com.sxh.usercenter.Model.domain.Team;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-11-29 21:10
 **/
@Data
public class TeamApplyVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2997908091286635973L;

    private Long id;

    private TeamUserVO teamUserVO;

    private UserVO user;

    private Date createTime;

    private String details;

    private Integer applyStatus;
}
