package com.example.demos.controller;

import com.example.demos.commons.BaseResponse;
import com.example.demos.commons.ErrorCode;
import com.example.demos.commons.ResultUtils;
import com.example.demos.exception.BaseException;
import com.example.demos.pojo.domain.Team;
import com.example.demos.request.TeamQueryRequest;
import com.example.demos.request.TeamUpdateRequest;
import com.example.demos.request.UserAddTeamRequest;
import com.example.demos.request.UserQuitTeamRequest;
import com.example.demos.service.TeamService;
import com.example.demos.vo.UserTeamVo;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/team")
public class TeamController {

    @Resource
    private TeamService teamService;

    /**
     * 添加队伍
     *
     * @param team
     * @param request
     * @return
     */
    @PostMapping("/addTeam")
    public BaseResponse<Long> addTeam(@RequestBody Team team, HttpServletRequest request) {
        //判断参数是否为空
        if (team == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        if (request == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        //调用业务
        long teamId = teamService.addTeam(team, request);
        if (teamId < 0) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(teamId);
    }

    @GetMapping("/select/team")
    public BaseResponse<List<UserTeamVo>> selectTeam(@RequestBody TeamQueryRequest teamQueryRequest, HttpServletRequest request) {
        if (teamQueryRequest == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        if (request == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        //调用业务
        List<UserTeamVo> userTeamVoList = teamService.selectTeam(teamQueryRequest, request);
        if (CollectionUtils.isEmpty(userTeamVoList) || userTeamVoList.size() < 0) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userTeamVoList);
    }

    @PostMapping("/update/team")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        if (request == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        //调用业务
        boolean result = teamService.updateTeam(teamUpdateRequest, request);
        if (!result) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(result);
    }

    @PostMapping("/userAdd")
    public BaseResponse<Integer> UserAddTeam(@RequestBody UserAddTeamRequest userAddTeamRequest, HttpServletRequest request) {
        if (userAddTeamRequest == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        if (request == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        int result = teamService.UserAddTeam(userAddTeamRequest, request);
        if (result < 0) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(result);
    }

    @PostMapping("/quit")
    public BaseResponse<Integer> UserQuitTeam(@RequestBody UserQuitTeamRequest userQuitTeamRequest, HttpServletRequest request) {
        if (userQuitTeamRequest == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        if (request == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        int result = teamService.UserQuitTeam(userQuitTeamRequest, request);
        if (result < 0) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(result);
    }

    @PostMapping("/disband")
    public BaseResponse<Integer> DisbandTeam(@RequestBody Long id, HttpServletRequest request) {
        if (id == null || id < 0) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        if (request == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        int result = teamService.DisbandTeam(id, request);
        if (result < 0) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(result);
    }

    @GetMapping("/get/joinTeam")
    public BaseResponse<List<Team>> getJoinTeam(HttpServletRequest request) {
        if (request == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        List<Team> userJoinTeam = teamService.getUserJoinTeam(request);
        if (CollectionUtils.isEmpty(userJoinTeam) || userJoinTeam.size() < 0) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userJoinTeam);
    }

    @GetMapping("/get/createTeam")
    public BaseResponse<List<Team>> getCreateTeam(HttpServletRequest request) {
        if (request == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        List<Team> userCreateTeam = teamService.getUserCreateTeam(request);
        if (CollectionUtils.isEmpty(userCreateTeam) || userCreateTeam.size() < 0) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userCreateTeam);
    }
    
}
