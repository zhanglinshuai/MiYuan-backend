package com.example.demos.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demos.commons.ErrorCode;
import com.example.demos.exception.BaseException;
import com.example.demos.mapper.TeamMapper;
import com.example.demos.mapper.UserteamMapper;
import com.example.demos.pojo.domain.Team;
import com.example.demos.pojo.domain.User;
import com.example.demos.pojo.domain.Userteam;
import com.example.demos.request.TeamQueryRequest;
import com.example.demos.request.TeamUpdateRequest;
import com.example.demos.request.UserAddTeamRequest;
import com.example.demos.service.TeamService;
import com.example.demos.service.UserService;
import com.example.demos.service.UserteamService;
import com.example.demos.vo.UserTeamVo;
import com.example.demos.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.demos.constants.TeamConstants.PRIVATE_STATUS;
import static com.example.demos.constants.TeamConstants.PROTECTED_STATUS;

/**
 * @author 86175
 * @description 针对表【team】的数据库操作Service实现
 * @createDate 2024-04-17 00:00:42
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private UserService userService;

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private UserteamMapper userteamMapper;

    @Override
    public long addTeam(Team team, HttpServletRequest request) {
        //非空校验
        if (team == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getUser(request);
        //参数判断
        int maxNum = team.getMaxNum();
        if (maxNum <= 0 || maxNum > 20) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        String teamName = team.getName();
        if (teamName.length() > 20) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        String description = team.getDescription();
        if (description.length() > 512) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        int teamStatus = team.getTeamStatus();
        if (teamStatus < 0) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        String password = team.getPassword();
        if (teamStatus == PROTECTED_STATUS) {
            if (password == null || password.length() > 32) {
                throw new BaseException(ErrorCode.PARAMS_ERROR);
            }
        }
        Date createTime = team.getCreateTime();
        if (createTime.before(new Date())) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Userteam> userteamQueryWrapper = new QueryWrapper<>();
        userteamQueryWrapper.eq("id", team.getId());
        Long userId = team.getUserId();
        userteamQueryWrapper.eq("userId", userId);
        Long count = userteamMapper.selectCount(userteamQueryWrapper);
        if (count > 5) {
            throw new BaseException(ErrorCode.NO_AUTH);
        }
        //插入队伍到队伍表
        int result = teamMapper.insert(team);
        if (result < 0 || team.getId() == null) {
            throw new BaseException(ErrorCode.SYSTEM_ERROR);
        }
        //将用户信息插入到 用户-队伍表中
        Userteam userteam = new Userteam();
        userteam.setUserId(team.getUserId());
        userteam.setTeamId(team.getId());
        userteam.setCreateTime(new Date());
        userteam.setUpdateTime(new Date());
        userteam.setJoinTime(new Date());
        int insert = userteamMapper.insert(userteam);
        if (insert < 0 || userteam.getId() == 0) {
            throw new BaseException(ErrorCode.SYSTEM_ERROR);
        }
        return team.getId();
    }

    @Override
    public List<UserTeamVo> selectTeam(TeamQueryRequest teamQueryRequest, HttpServletRequest request) {

        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        if (teamQueryRequest == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        String teamName = teamQueryRequest.getName();
        if (teamName != null && teamName.length() <= 20) {
            teamQueryWrapper.eq("name", teamName);
        }
        int teamStatus = teamQueryRequest.getTeamStatus();
        String password = teamQueryRequest.getPassword();
        //如果用户是管理员
        if (userService.isAdmin(request)) {
            //如果队伍的状态码是加密并且队伍有密码
            if (teamStatus == PROTECTED_STATUS) {
                if (password != null || password.length() <= 32) {
                    teamQueryWrapper.eq("teamStatus", teamStatus);
                }
            }
        }
        String description = teamQueryRequest.getDescription();
        //如果队伍有队伍名并且描述符合规则
        if (description != null && description.length() <= 512) {
            if (teamName != null && teamName.length() > 20) {
                teamQueryWrapper.and(qw -> qw.like("name", teamName).or().like("description", description));
            }
        }
        //如果过期时间小于当前时间，就不展示
        //expireTime is null or expireTime>now()
        teamQueryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        List<Team> TeamList = this.list(teamQueryWrapper);
        if (CollectionUtils.isEmpty(TeamList)) {
            return new ArrayList<>();
        }
        List<UserTeamVo> userTeamVoList = new ArrayList<>();

        for (Team team : TeamList) {
            //从队伍中获取创建人信息
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            UserTeamVo userTeamVo = new UserTeamVo();
            BeanUtils.copyProperties(team, userTeamVo);
            if (user != null) {
                UserVo userVo = new UserVo();
                BeanUtils.copyProperties(user, userVo);
                userTeamVo.setCreateUser(userVo);
            }
            userTeamVoList.add(userTeamVo);
        }
        return userTeamVoList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        //判断非空
        if (teamUpdateRequest == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        //判断队伍是否存在
        Long id = teamUpdateRequest.getId();
        if (id == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        //如果队伍存在，只允许管理员或者队伍创建者可以修改
        Long createUserId = teamUpdateRequest.getUserId();
        User user = userService.getUser(request);
        Long loginUserId = user.getId();
        if (userService.isAdmin(request) || createUserId.equals(loginUserId)) {
            //如果新队伍和老队伍，相等就不执行更新
            //老队伍
            Team OldTeam = teamMapper.selectById(id);
            //新队伍
            Team team = new Team();
            BeanUtils.copyProperties(teamUpdateRequest, team);
            if (OldTeam.equals(team)) {
                return false;
            }
            QueryWrapper<Team> queryWrapper = new QueryWrapper<Team>().eq("id", id);
            boolean update = this.update(queryWrapper);
            if (!update) {
                throw new BaseException(ErrorCode.SYSTEM_ERROR);
            }
        }
        return true;
    }

    @Override
    public int UserAddTeam(UserAddTeamRequest userAddTeamRequest, HttpServletRequest request) {
        //非空判断
        if (userAddTeamRequest == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        if (request == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        //条件校验
        //用户必须存在
        Long teamId = userAddTeamRequest.getId();
        Long userId = userAddTeamRequest.getUserId();
        User loginUser = userService.getUser(request);
        Long loginUserId = loginUser.getId();
        if (teamId == null || teamId <= 0) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        //用户最多加入5个队伍
        QueryWrapper<Userteam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUserId);
        long count = userteamMapper.selectCount(queryWrapper);
        if (count > 5) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        //用户只能加入未过期的队伍
        Date expireTime = userAddTeamRequest.getExpireTime();
        if (expireTime == null || expireTime.before(new Date())) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        //用户只能加入未满的队伍
        int hasJoinNum = userAddTeamRequest.getHasJoinUserNum();
        Integer maxNum = userAddTeamRequest.getMaxNum();
        if (hasJoinNum >= maxNum) {
            throw new BaseException(ErrorCode.NO_AUTH);
        }
        //不能加入自己的队伍
        if (loginUserId.equals(userId)) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        //不能重复加入队伍
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUserId);
        queryWrapper.eq("teamId", teamId);
        Long result = userteamMapper.selectCount(queryWrapper);
        if (result > 0) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        //禁止加入私有的队伍
        Integer teamStatus = userAddTeamRequest.getTeamStatus();
        if (teamStatus.equals(PRIVATE_STATUS)) {
            throw new BaseException(ErrorCode.NO_AUTH);
        }
        //如果加入的队伍是加密的，必须密码匹配才可以
        Team team = teamMapper.selectById(teamId);
        String password = team.getPassword();
        String userAddTeamRequestPassword = userAddTeamRequest.getPassword();
        if (teamStatus.equals(PROTECTED_STATUS)) {
            if (StringUtils.isBlank(password) || !userAddTeamRequestPassword.equals(password)) {
                throw new BaseException(ErrorCode.NO_AUTH);
            }
        }
        //新增队伍，用户关联信息
        Userteam userteam = new Userteam();
        userteam.setUserId(loginUserId);
        userteam.setTeamId(teamId);
        return userteamMapper.insert(userteam);
    }

    
}




