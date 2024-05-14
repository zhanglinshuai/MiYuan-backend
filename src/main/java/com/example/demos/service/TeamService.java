package com.example.demos.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demos.pojo.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demos.request.TeamQueryRequest;
import com.example.demos.request.TeamUpdateRequest;
import com.example.demos.vo.UserTeamVo;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 86175
 * @description 针对表【team】的数据库操作Service
 * @createDate 2024-04-17 00:00:42
 */
public interface TeamService extends IService<Team> {


    /**
     * 添加队伍列表
     *
     * @param team
     * @param request
     * @return
     */
    long addTeam(Team team, HttpServletRequest request);

    /**
     * 查询用户
     * @param teamQueryRequest
     * @param request
     * @return
     */
    List<UserTeamVo> selectTeam(TeamQueryRequest teamQueryRequest,HttpServletRequest request);

    /**
     * 修改用户信息
     * @param teamUpdateRequest
     * @param request
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request);



}
