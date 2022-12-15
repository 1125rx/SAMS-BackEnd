package com.sxh.usercenter.service;

import com.sxh.usercenter.Model.VO.TeamApplyVO;
import com.sxh.usercenter.Model.VO.TeamUserVO;
import com.sxh.usercenter.Model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sxh.usercenter.Model.domain.User;
import com.sxh.usercenter.Model.dto.TeamQuery;
import com.sxh.usercenter.Model.request.team.TeamJoinRequest;
import com.sxh.usercenter.Model.request.team.TeamQuitRequest;
import com.sxh.usercenter.Model.request.team.TeamUpdateRequest;
import com.sxh.usercenter.Model.request.team.TeamWelcomeRequest;

import java.util.List;

/**
* @author sxh
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2022-11-22 14:48:35
*/
public interface TeamService extends IService<Team> {

    long createTeam(Team team, User user);

    List<TeamUserVO> listTeams(TeamQuery teamQuery,boolean isAdmin);

    boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser);

    boolean applyJoinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    boolean welcomeTeam(TeamWelcomeRequest teamWelcomeRequest,User loginUser);

    List<TeamApplyVO> getWelcomeList(User loginUser);

    boolean dealApply(Long id, User loginUser, int applyStatus);

    boolean dealWelcome(Long id,User loginUser,int applyStatus);

    boolean quitTeam(TeamQuitRequest teamQuitRequest,User loginUser);

    boolean deleteTeam(long id, User loginUser);

    List<TeamApplyVO> getApplyList(User loginUser);

    List<TeamApplyVO> getMyApplyHistory(User loginUser);

    long countTeamUserByTeamId(long teamId);

    Team getTeamById(Long teamId);



}
