package com.sxh.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sxh.usercenter.Model.VO.TeamApplyVO;
import com.sxh.usercenter.Model.VO.TeamUserVO;
import com.sxh.usercenter.Model.VO.UserVO;
import com.sxh.usercenter.Model.domain.Team;
import com.sxh.usercenter.Model.domain.User;
import com.sxh.usercenter.Model.domain.UserTeam;
import com.sxh.usercenter.Model.dto.TeamQuery;
import com.sxh.usercenter.Model.enums.TeamStatusEnum;
import com.sxh.usercenter.Model.request.team.TeamJoinRequest;
import com.sxh.usercenter.Model.request.team.TeamQuitRequest;
import com.sxh.usercenter.Model.request.team.TeamUpdateRequest;
import com.sxh.usercenter.Model.request.team.TeamWelcomeRequest;
import com.sxh.usercenter.common.ErrorCode;
import com.sxh.usercenter.exception.BusinessException;
import com.sxh.usercenter.service.TeamService;
import com.sxh.usercenter.Mapper.TeamMapper;
import com.sxh.usercenter.service.UserService;
import com.sxh.usercenter.service.UserTeamService;
import com.sxh.usercenter.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/*
 * @author sxh
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2022-11-22 14:48:35
 */
@Service
@Slf4j
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private UserService userService;

    @Resource
    private UserTeamService userTeamService;

    @Override
    public long createTeam(Team team, User user) {
        if (team == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未获取队伍参数");
        if (user == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未获取用户信息");
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("t_userId", user.getUserId());
        long hasTeamNum = teamMapper.selectCount(queryWrapper);
        if (hasTeamNum > 5)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍数量已达上限");
        team.setT_userId(user.getUserId());
        //如果未设置失效时间，默认为一个月后失效
        if (team.getT_expireTime() == null)
            team.setT_expireTime(DateUtils.getMonthAfterNow());
        if (team.getT_avatarUrl() == null)
            team.setT_avatarUrl("https://i.postimg.cc/FsP4bXJr/images.jpg");
        boolean save = this.save(team);
        long teamId = team.getT_id();
        if (!save)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建失败");
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(user.getUserId());
        userTeam.setJoinTime(new Date());
        userTeam.setCreateTime(new Date());
        userTeam.setUpdateTime(new Date());
        userTeam.setApplyStatus(1);
        boolean save1 = userTeamService.save(userTeam);
        if (!save1)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建失败");
        return team.getT_id();
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("t_id", id);
            }
            List<Long> idList = teamQuery.getIdList();
            if (!CollectionUtils.isEmpty(idList))
                queryWrapper.in("t_id", idList);
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText))
                queryWrapper.and(qw -> qw.like("t_name", searchText).or().like("t_description", searchText));
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description))
                queryWrapper.like("t_description", description);
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0)
                queryWrapper.eq("t_maxNum", maxNum);
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0)
                queryWrapper.eq("t_userId", userId);
            Integer status = teamQuery.getStatus();
            TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
            if (teamStatusEnum == null)
                teamStatusEnum = TeamStatusEnum.PUBLIC;
            if (!isAdmin && teamStatusEnum.equals(TeamStatusEnum.PRIVATE))
                throw new BusinessException(ErrorCode.NO_AUTH);
            if (status != null)
                queryWrapper.eq("t_status", status);

        }
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList))
            return new ArrayList<>();
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        for (Team team : teamList) {
            Long userId = team.getT_userId();
            if (userId == null)
                continue;
            User user = userService.getById(userId);
            List<UserVO> members = new ArrayList<>();
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("teamId", team.getT_id());
            userTeamQueryWrapper.eq("applyStatus", 1);
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);

            for (UserTeam userTeam : userTeamList) {
                if (Objects.equals(userTeam.getUserId(), team.getT_userId()))
                    continue;
                User resUser = userService.getById(userTeam.getUserId());
                members.add(userService.getUserVO(resUser));
            }
            long hasJoinNum = this.countTeamUserByTeamId(team.getT_id());
            TeamUserVO teamUserVO = new TeamUserVO();
            teamUserVO.setHasJoinNum(hasJoinNum);
            teamUserVO.setMembers(members);
            BeanUtils.copyProperties(team, teamUserVO);
            if (user != null) {
                UserVO userVO = userService.getUserVO(user);
                teamUserVO.setT_createUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        Long id = teamUpdateRequest.getT_id();
        if (id == null || id <= 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        Team oldTeam = this.getById(id);
        if (oldTeam == null)
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        if (oldTeam.getT_userId() != loginUser.getUserId())
            throw new BusinessException(ErrorCode.NO_AUTH);
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getT_status());
        if (teamStatusEnum.equals(TeamStatusEnum.SECRET))
            if (StringUtils.isBlank(teamUpdateRequest.getT_password()))
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密队伍需要设置密码");
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        return this.updateById(updateTeam);
    }

    @Override
    /*
     * @Description: 申请加入队伍
     * @Param: [teamJoinRequest, loginUser]
     * @return:
     * @Author: SXH
     * @Date: 2022/11/29
     */
    public boolean applyJoinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        Long id = teamJoinRequest.getT_id();
        Team team = getTeamById(id);
        Date expireTime = team.getT_expireTime();
        if (expireTime != null && expireTime.before(new Date()))
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        Integer status = team.getT_status();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私有队伍");
        }
//        String password = teamJoinRequest.getT_password();
//        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)) {
//            if (StringUtils.isBlank(password) || !password.equals(team.getT_password())) {
//                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
//            }
//        }
        // 该用户已加入的队伍数量
        long userId = loginUser.getUserId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        userTeamQueryWrapper.eq("applyStatus", 1);
        long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
        if (hasJoinNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建和加入 5 个队伍");
        }
        // 不能重复加入已加入的队伍
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        userTeamQueryWrapper.eq("teamId", id);
        userTeamQueryWrapper.eq("applyStatus", 1);
        long hasUserJoinTeam = userTeamService.count(userTeamQueryWrapper);
        if (hasUserJoinTeam > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已加入该队伍");
        }
        //不能重复申请加入同一支队伍
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        userTeamQueryWrapper.eq("teamId", id);
        userTeamQueryWrapper.eq("applyStatus", 0);
        long hasUserApplyTeam = userTeamService.count(userTeamQueryWrapper);
        if (hasUserApplyTeam > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已申请加入该队伍");
        }
        // 已加入队伍的人数
        long teamHasJoinNum = this.countTeamUserByTeamId(id);
        if (teamHasJoinNum >= team.getT_maxNum()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满");
        }
        // 修改队伍信息
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(id);
        userTeam.setCreateTime(new Date());
        userTeam.setApplyStatus(0);
        userTeam.setDetails(teamJoinRequest.getDetails());
        return userTeamService.save(userTeam);
    }

    @Override
    public boolean welcomeTeam(TeamWelcomeRequest teamWelcomeRequest, User loginUser) {
        if (teamWelcomeRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        Long id = teamWelcomeRequest.getTeamId();
        Team team = getTeamById(id);
        Date expireTime = team.getT_expireTime();
        if (team.getT_userId() != loginUser.getUserId())
            throw new BusinessException(ErrorCode.NO_AUTH, "您没有权限");
        if (expireTime != null && expireTime.before(new Date()))
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        long userId = teamWelcomeRequest.getUserId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        userTeamQueryWrapper.eq("applyStatus", 1);
        long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
        if (hasJoinNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该用户加入队伍数量已达上限");
        }
        // 不能重复加入已加入的队伍
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        userTeamQueryWrapper.eq("teamId", id);
        userTeamQueryWrapper.eq("applyStatus", 1);
        long hasUserJoinTeam = userTeamService.count(userTeamQueryWrapper);
        if (hasUserJoinTeam > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已加入该队伍");
        }
        //不能重复申请加入同一支队伍
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        userTeamQueryWrapper.eq("teamId", id);
        userTeamQueryWrapper.eq("applyStatus", 3);
        long hasUserApplyTeam = userTeamService.count(userTeamQueryWrapper);
        if (hasUserApplyTeam > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已邀请该用户加入队伍");
        }
        // 已加入队伍的人数
        long teamHasJoinNum = this.countTeamUserByTeamId(id);
        if (teamHasJoinNum >= team.getT_maxNum()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满");
        }
        // 修改队伍信息
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(id);
        userTeam.setCreateTime(new Date());
        userTeam.setApplyStatus(3);
        userTeam.setDetails(teamWelcomeRequest.getDescription());
        return userTeamService.save(userTeam);
    }

    @Override
    public List<TeamApplyVO> getWelcomeList(User loginUser) {
        if (loginUser == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        long userId = loginUser.getUserId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.ge("applyStatus", 1);
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        if (userTeamList == null)
            return new ArrayList<>();
        List<TeamApplyVO> teamList = new ArrayList<>();
        for (UserTeam userTeam : userTeamList) {
            Team team=this.getById(userTeam.getTeamId());
            if (team.getT_userId() == loginUser.getUserId() && userTeam.getUserId() == loginUser.getUserId())
                continue;
            TeamApplyVO teamApplyVO = new TeamApplyVO();
            teamApplyVO.setId(userTeam.getId());
            teamApplyVO.setUser(userService.getUserVO(userService.getById(this.getTeamById(userTeam.getTeamId()).getT_userId())));
            teamApplyVO.setApplyStatus(userTeam.getApplyStatus());
            teamApplyVO.setDetails(userTeam.getDetails());
            teamApplyVO.setCreateTime(userTeam.getCreateTime());
            TeamQuery teamQuery = new TeamQuery();
            teamQuery.setId(userTeam.getTeamId());
            teamApplyVO.setTeamUserVO(this.listTeams(teamQuery, true).get(0));
            teamList.add(teamApplyVO);
        }
        return teamList;

    }

    @Override
    /*
     * @Description: 处理申请
     * @Param: [id, loginUser, applyStatus]
     * @return:
     * @Author: SXH
     * @Date: 2022/11/29
     */
    public boolean dealApply(Long id, User loginUser, int applyStatus) {
        if (id == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        UserTeam userTeam = userTeamService.getById(id);
        Team team = this.getTeamById(userTeam.getTeamId());
        if (loginUser.getUserId() != team.getT_userId())
            throw new BusinessException(ErrorCode.NO_AUTH);
        userTeam.setApplyStatus(applyStatus);
        return userTeamService.updateById(userTeam);
    }

    @Override
    public boolean dealWelcome(Long id, User loginUser, int applyStatus) {
        if (id == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        UserTeam userTeam = userTeamService.getById(id);
        User user = userService.getById(userTeam.getUserId());
        if (loginUser.getUserId() != user.getUserId())
            throw new BusinessException(ErrorCode.NO_AUTH);
        userTeam.setApplyStatus(applyStatus);
        return userTeamService.updateById(userTeam);
    }

    @Override
    /*
     * @Description: 退出队伍
     * @Param: [teamQuitRequest, loginUser]
     * @return:
     * @Author: SXH
     * @Date: 2022/11/29
     */
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitRequest.getT_id();
        Team team = getTeamById(teamId);
        long userId = loginUser.getUserId();
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setTeamId(teamId);
        queryUserTeam.setUserId(userId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(queryUserTeam);
        long count = userTeamService.count(queryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入队伍");
        }
        long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
        // 队伍只剩一人，解散
        if (teamHasJoinNum == 1) {
            // 删除队伍
            this.removeById(teamId);
        } else {
            // 队伍还剩至少两人
            // 是队长
            if (team.getT_userId() == userId) {
                // 把队伍转移给最早加入的用户
                // 1. 查询已加入队伍的所有用户和加入时间
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                // 更新当前队伍的队长
                Team updateTeam = new Team();
                updateTeam.setT_id(teamId);
                updateTeam.setT_userId(nextTeamLeaderId);
                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍队长失败");
                }
            }
        }
        // 移除关系
        return userTeamService.remove(queryWrapper);
    }

    @Override
    /*
     * @Description: 删除队伍
     * @Param: [id, loginUser]
     * @return:
     * @Author: SXH
     * @Date: 2022/11/29
     */
    public boolean deleteTeam(long id, User loginUser) {
        // 校验队伍是否存在
        Team team = getTeamById(id);
        long teamId = team.getT_id();
        // 校验你是不是队伍的队长
        if (team.getT_userId() != loginUser.getUserId()) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无访问权限");
        }
        // 移除所有加入队伍的关联信息
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        boolean result = userTeamService.remove(userTeamQueryWrapper);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍关联信息失败");
        }
        // 删除队伍
        return this.removeById(teamId);
    }

    @Override
    public List<TeamApplyVO> getApplyList(User loginUser) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long userId = loginUser.getUserId();
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("t_userId", userId);
        List<Team> teamList = this.list(teamQueryWrapper);
        if (CollectionUtils.isEmpty(teamList))
            return new ArrayList<>();
        List<TeamApplyVO> teamApplyVOList = new ArrayList<>();
        for (Team team : teamList) {
            Long teamId = team.getT_id();
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("teamId", teamId);
            userTeamQueryWrapper.le("applyStatus",2);
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            if (CollectionUtils.isEmpty(userTeamList))
                continue;
            for (UserTeam userTeam : userTeamList) {
                if (userTeam.getUserId().equals(loginUser.getUserId()))
                    continue;
                User user = userService.getById(userTeam.getUserId());
                UserVO userVO = userService.getUserVO(user);
                TeamApplyVO teamApplyVO = new TeamApplyVO();
                BeanUtils.copyProperties(userTeam, teamApplyVO);
                teamApplyVO.setUser(userVO);
                TeamQuery teamQuery = new TeamQuery();
                teamQuery.setId(team.getT_id());
                teamApplyVO.setTeamUserVO(this.listTeams(teamQuery, true).get(0));
                teamApplyVOList.add(teamApplyVO);
            }
        }
        return teamApplyVOList;
    }

    @Override
    public List<TeamApplyVO> getMyApplyHistory(User loginUser) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<UserTeam> userTeamList = userTeamService.list();
        if (userTeamList == null)
            return new ArrayList<>();
        List<TeamApplyVO> teamApplyVOList = new ArrayList<>();
        for (UserTeam userTeam : userTeamList) {
            Team team = this.getById(userTeam.getTeamId());
            if (team.getT_userId() == loginUser.getUserId() || userTeam.getUserId() == loginUser.getUserId()){
                if (team.getT_userId() == loginUser.getUserId() && userTeam.getUserId() == loginUser.getUserId()){
                    continue;
                }
                TeamApplyVO teamApplyVO = new TeamApplyVO();
                BeanUtils.copyProperties(userTeam, teamApplyVO);
                UserVO userVO = userService.getUserVO(userService.getById(userTeam.getUserId()));
                teamApplyVO.setUser(userVO);
                TeamQuery teamQuery = new TeamQuery();
                teamQuery.setId(userTeam.getTeamId());
                teamApplyVO.setTeamUserVO(this.listTeams(teamQuery, true).get(0));
                teamApplyVOList.add(teamApplyVO);
            }
        }
        return teamApplyVOList;
    }

    /*
     * @Description: 通过id获取队伍
     * @Param:
     * @return:
     * @Author: SXH
     * @Date: 2022/11/29
     */
    public Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        Team team = this.getById(teamId);
        if (team == null)
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        return team;
    }

    /*
     * @Description: 获取队伍当前人数
     * @Param:
     * @return:
     * @Author: SXH
     * @Date: 2022/11/29
     */
    public long countTeamUserByTeamId(long teamId) {
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        userTeamQueryWrapper.eq("applyStatus", 1);
        return userTeamService.count(userTeamQueryWrapper);
    }


}




