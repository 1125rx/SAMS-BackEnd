package com.sxh.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sxh.usercenter.Mapper.TeamMapper;
import com.sxh.usercenter.Model.VO.TeamApplyVO;
import com.sxh.usercenter.Model.VO.TeamUserVO;
import com.sxh.usercenter.Model.domain.Team;
import com.sxh.usercenter.Model.domain.User;
import com.sxh.usercenter.Model.domain.UserTeam;
import com.sxh.usercenter.Model.dto.TeamQuery;
import com.sxh.usercenter.Model.request.team.*;
import com.sxh.usercenter.Model.request.user.UpdateUserRequest;
import com.sxh.usercenter.common.BaseResponse;
import com.sxh.usercenter.common.ErrorCode;
import com.sxh.usercenter.common.ResultUtils;
import com.sxh.usercenter.exception.BusinessException;
import com.sxh.usercenter.service.TeamService;
import com.sxh.usercenter.service.UserService;
import com.sxh.usercenter.service.UserTeamService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-11-29 08:59
 **/
@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {
    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private TeamMapper teamMapper;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        long teamId = teamService.createTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    @PostMapping("/set/welcome/pass")
    public BaseResponse<Boolean> passTeamWelcome(@RequestBody TeamWelcomeDealRequest teamWelcomeDealRequest,HttpServletRequest request){
        User loginUser=userService.getLoginUser(request);
        UserTeam userTeam=userTeamService.getById(teamWelcomeDealRequest.getId());
        long teamId = userTeam.getTeamId();
        int maxNum=teamService.getById(teamId).getT_maxNum();
        long teamHasJoinNum=teamService.countTeamUserByTeamId(teamId);
        if (teamHasJoinNum >= maxNum) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已满");
        }
        boolean dealApply = teamService.dealWelcome(teamWelcomeDealRequest.getId(), loginUser, 1);
        return ResultUtils.success(dealApply);
    }

    @PostMapping("/set/welcome/refuse")
    public BaseResponse<Boolean> refuseTeamWelcome(@RequestBody TeamWelcomeDealRequest teamWelcomeDealRequest,HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        boolean b = teamService.dealWelcome(teamWelcomeDealRequest.getId(), loginUser, 2);
        return  ResultUtils.success(b);
    }



    @PostMapping("/set/apply/pass")
    public BaseResponse<Boolean> passTeamApply(@RequestBody TeamApplyDealRequest teamApplyDealRequest,HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        UserTeam userTeam=userTeamService.getById(teamApplyDealRequest.getId());
        long teamId = userTeam.getTeamId();
        int maxNum=teamService.getById(teamId).getT_maxNum();
        long teamHasJoinNum=teamService.countTeamUserByTeamId(teamId);
        if (teamHasJoinNum >= maxNum) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已满");
        }
        boolean dealApply = teamService.dealApply(teamApplyDealRequest.getId(), loginUser, 1);
        return ResultUtils.success(dealApply);
    }

    @PostMapping("/set/apply/refuse")
    public BaseResponse<Boolean> refuseTeamApply(@RequestBody TeamApplyDealRequest teamApplyDealRequest,HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        boolean b = teamService.dealApply(teamApplyDealRequest.getId(), loginUser, 2);
        return  ResultUtils.success(b);
    }

    @PostMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(@RequestBody TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        // 1、查询队伍列表
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, isAdmin);
        final List<Long> teamIdList = teamList.stream().map(TeamUserVO::getT_id).collect(Collectors.toList());
        // 2、判断当前用户是否已加入队伍
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        try {
            User loginUser = userService.getLoginUser(request);
            userTeamQueryWrapper.eq("userId", loginUser.getUserId());
            userTeamQueryWrapper.in("teamId", teamIdList);
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            // 已加入的队伍 id 集合
            Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            teamList.forEach(team -> {
                boolean hasJoin = hasJoinTeamIdSet.contains(team.getT_id());
                team.setHasJoin(hasJoin);
            });
        } catch (Exception e) {}
        // 3、查询已加入队伍的人数
        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
        userTeamJoinQueryWrapper.in("teamId", teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(userTeamJoinQueryWrapper);
        // 队伍 id => 加入这个队伍的用户列表
        Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        teamList.forEach(team -> team.setHasJoinNum(teamIdUserTeamList.getOrDefault(team.getT_id(), new ArrayList<>()).size()));
        return ResultUtils.success(teamList);
    }

//    // todo 查询分页
//    @GetMapping("/list/page")
//    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery) {
//        if (teamQuery == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        Team team = new Team();
//        BeanUtils.copyProperties(teamQuery, team);
//        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
//        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
//        Page<Team> resultPage = teamService.page(page, queryWrapper);
//        return ResultUtils.success(resultPage);
//    }

    @PostMapping("/join")
    /**
    * @Description: 申请加入队伍
    * @Param: [teamJoinRequest, request]
    * @return:
    * @Author: SXH
    * @Date: 2022/11/29
    */
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.applyJoinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/welcome")
    /*
    * @Description: 邀请加入队伍
    * @Param: [teamWelcomeRequest, request]
    * @return:
    * @Author: SXH
    * @Date: 2022/12/14
    */
    public BaseResponse<Boolean> welcomeTeamController(@RequestBody TeamWelcomeRequest teamWelcomeRequest, HttpServletRequest request){
        if (teamWelcomeRequest==null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        boolean welcomeTeam = teamService.welcomeTeam(teamWelcomeRequest, loginUser);
        return ResultUtils.success(welcomeTeam);

    }

    @PostMapping("/get/teamVO")
    public BaseResponse<TeamUserVO> getTeamUserVOById(TeamDeleteRequest request){
        if (request==null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        long teamId=request.getT_id();
        TeamQuery teamQuery=new TeamQuery();
        teamQuery.setId(teamId);
        List<TeamUserVO> teamUserVOS = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(teamUserVOS.get(0));
    }

    @PostMapping("/quit")
    /*
    * @Description: 退出
    * @Param: [teamQuitRequest, request]
    * @return:
    * @Author: SXH
    * @Date: 2022/11/29
    */
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    /*
    * @Description: 删除
    * @Param: [deleteRequest, request]
    * @return:
    * @Author: SXH
    * @Date: 2022/11/29
    */
    public BaseResponse<Boolean> deleteTeam(@RequestBody TeamDeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getT_id() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getT_id();
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(id, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }


    /**
     * 获取我创建的队伍
     *
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listMyCreateTeams(HttpServletRequest request) {
        TeamQuery teamQuery=new TeamQuery();
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getUserId());
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(teamList);
    }

    @GetMapping("/list/get/applyList")
    public BaseResponse<List<TeamApplyVO>> listTeamApply(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        List<TeamApplyVO> teamApplyVOList=teamService.getApplyList(loginUser);
        return ResultUtils.success(teamApplyVOList);
    }


    @GetMapping("/list/get/welcome")
    public BaseResponse<List<TeamApplyVO>> listTeamWelcome(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        List<TeamApplyVO> teamApplyVOList = teamService.getWelcomeList(loginUser);
        return ResultUtils.success(teamApplyVOList);
    }

    @GetMapping("/list/get/history")
    public BaseResponse<List<TeamApplyVO>> listMyHistory(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        List<TeamApplyVO> myApplyHistory = teamService.getMyApplyHistory(loginUser);
        return ResultUtils.success(myApplyHistory);
    }




    /**
     * 获取我加入的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @PostMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(@RequestBody TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getUserId());
        queryWrapper.eq("applyStatus",1);
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        if (userTeamList.size()==0)
            return ResultUtils.success(new ArrayList<>());
        // 取出不重复的队伍 id
        // teamId userId
        // 1, 2
        // 1, 3
        // 2, 3
        // result
        // 1 => 2, 3
        // 2 => 3
        Map<Long, List<UserTeam>> listMap = userTeamList.stream()
                .collect(Collectors.groupingBy(UserTeam::getTeamId));
        List<Long> idList = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idList);
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(teamList);
    }

}
