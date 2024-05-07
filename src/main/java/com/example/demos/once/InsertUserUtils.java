package com.example.demos.util;
import java.util.Date;

import com.example.demos.pojo.domain.User;
import com.example.demos.service.UserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量插入用户工具类
 */
@Component
public class InsertUserUtils {

    @Resource
    private UserService userService;

    public static void insertUser() {
        final int INSET_NUM = 1000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSET_NUM; i++) {
            User user = new User();
            user.setUsername("");
            user.setUserAccount("");
            user.setUserPassword("");
            user.setAvatarUrl("");
            user.setGender(0);
            user.setUserRole(0);
            user.setPlanetCode("");
            user.setEmail("");
            user.setPhone("");
            user.setTags("");
            user.setProfile("");
            userList.add(user);
        }


    }


}
