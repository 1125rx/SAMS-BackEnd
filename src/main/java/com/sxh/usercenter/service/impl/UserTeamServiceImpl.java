package com.sxh.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sxh.usercenter.Model.domain.UserTeam;
import com.sxh.usercenter.service.UserTeamService;
import com.sxh.usercenter.Mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author sxh
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2022-11-22 15:15:35
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




