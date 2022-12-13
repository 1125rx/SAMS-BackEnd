package com.sxh.usercenter.Model.request.team;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-11-29 09:06
 **/
@Data
public class TeamDeleteRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 8160973212847991253L;

    private long t_id;
}
