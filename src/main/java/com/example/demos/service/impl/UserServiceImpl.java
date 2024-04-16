package com.example.demos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demos.pojo.domain.User;
import com.example.demos.service.UserService;
import com.example.demos.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 86175
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-04-17 00:02:53
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




