package com.example.demos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demos.mapper.TeamMapper;
import com.example.demos.pojo.domain.Team;
import com.example.demos.service.TeamService;
import org.springframework.stereotype.Service;

/**
* @author 86175
* @description 针对表【team】的数据库操作Service实现
* @createDate 2024-04-17 00:00:42
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




