package com.example.demos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demos.pojo.domain.Userteam;
import com.example.demos.service.UserteamService;
import com.example.demos.mapper.UserteamMapper;
import org.springframework.stereotype.Service;

/**
* @author 86175
* @description 针对表【userteam】的数据库操作Service实现
* @createDate 2024-04-17 00:03:40
*/
@Service
public class UserteamServiceImpl extends ServiceImpl<UserteamMapper, Userteam>
    implements UserteamService{

}




