package com.example.demos.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demos.commons.BaseResponse;
import com.example.demos.commons.ErrorCode;
import com.example.demos.commons.ResultUtils;
import com.example.demos.exception.BaseException;
import com.example.demos.pojo.domain.User;
import com.example.demos.request.LoginRequest;
import com.example.demos.request.RegisterRequest;
import com.example.demos.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequestMapping("/user")
@RestController
@CrossOrigin(origins = {"http://localhost:5173/"}, allowCredentials = "true")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody RegisterRequest registerRequest) {
        //请求参数非空校验
        if (registerRequest == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        String userAccount = registerRequest.getUserAccount();
        String userPassword = registerRequest.getUserPassword();
        String planetCode = registerRequest.getPlanetCode();
        String checkPassword = registerRequest.getCheckPassword();
        //参数非空校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        //账号长度最少为4位
        if (userAccount.length() < 4) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "账号长度过短");
        }
        //密码长度最少为6位
        if (userPassword.length() < 6 || checkPassword.length() < 6) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "密码长度过短");
        }
        //密码与校验密码是否相等
        if (!userPassword.equals(checkPassword)) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "密码与校验密码不相等");
        }
        //星球编号长度最多为5位
        if (planetCode.length() > 4) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "星球编号长度过长");
        }
        //校验用户账号是否含有非法字符
        boolean result = userService.verifyUserAccount(userAccount);
        if (!result) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "用户账号含有特殊字符");
        }
        long userId = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        if (userId < 0) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userId);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        //用户非空判断
        if (loginRequest == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        String userAccount = loginRequest.getUserAccount();
        String userPassword = loginRequest.getUserPassword();
        //参数非空判断
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        //账号长度不少于4位
        if (userAccount.length() < 4) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "账号长度过短");
        }
        //密码不少于6位
        if (userPassword.length() < 6) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "密码长度过短");
        }
        //校验用户账号是否含有特殊字符
        boolean result = userService.verifyUserAccount(userAccount);
        if (!result) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "账号含有特殊字符");
        }
        User safetyUser = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(safetyUser);
    }


    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        if (!result) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(true);
    }


    @GetMapping("/select/name")
    public BaseResponse<User> selectUserByName(String username, HttpServletRequest request) {
        //参数判空
        if (StringUtils.isBlank(username)) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (request == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        //返回脱敏后的用户信息
        User user = userService.queryUserByName(username, request);
        if (user == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(user);
    }

    @GetMapping("/getCurrent")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        if (request == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        User currentUser = userService.getCurrentUser(request);
        if (currentUser == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "无法获取当前用户");
        }
        return ResultUtils.success(currentUser);
    }

    @GetMapping("/get/users")
    public BaseResponse<List<User>> getUsers(HttpServletRequest request) {
        if (request == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.getUserList(request);
        if (CollectionUtils.isEmpty(userList)) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userList);
    }

    @PostMapping("/deleteById")
    public BaseResponse<Boolean> deleteUserById(long id, HttpServletRequest request) {
        if (id < 0) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.deleteUserById(id, request);
        if (!result) {
            throw new BaseException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTag(List<String> tagList) {
        if (CollectionUtils.isEmpty(tagList)) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.selectUserByTagsBySQL(tagList);
        return ResultUtils.success(userList);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        if (request == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        if (user == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        //调用业务
        int result = userService.updateUserInfo(user, request);
        return ResultUtils.success(result);
    }

    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUser(long pageNum, long pageSize, HttpServletRequest request) {
        //缓存预热
        //设计缓存的key MiYuan:user:recommend:userId
        User user = userService.getUser(request);
        String redisKey = String.format("MiYuan:user:recommend:%s", user.getId());
        //先判断是否存在缓存，如果存在就读缓存
        Page<User> userPage = (Page<User>) redisTemplate.opsForValue().get(redisKey);
        if (userPage != null) {
            return ResultUtils.success(userPage);
        }
        //如果不存在就读数据库
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum, pageSize), userQueryWrapper);
        //读完数据库后写缓存
        try {
            redisTemplate.opsForValue().set(redisKey, userPage,30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResultUtils.success(userPage);
    }


}
