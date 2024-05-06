package com.example.demos.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demos.commons.ErrorCode;
import com.example.demos.dto.UserDTO;
import com.example.demos.exception.BaseException;
import com.example.demos.pojo.domain.User;
import com.example.demos.service.UserService;
import com.example.demos.mapper.UserMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.demos.constants.UserConstants.*;
import static com.example.demos.constants.UserConstants.USER_LOGIN_STATUS;

/**
 * @author 86175
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2024-04-17 00:02:53
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @param planetCode
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        //非空校验
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
            throw new BaseException(ErrorCode.PARAMS_ERROR, "密码与校验密码不同");
        }
        //星球编号长度最多为5位
        if (planetCode.length() > 4) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "星球编号过长");
        }
        //校验账号中是否含有非法字符
        boolean result = verifyUserAccount(userAccount);
        if (!result) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "账号含有特殊字符");
        }
        //对密码进行加密
        String safetyPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());
        //查询账号是否重复
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        Long count = userMapper.selectCount(userQueryWrapper);
        //如果count>0说明账号重复了
        if (count > 0) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "账号重复了");
        }
        //创建新用户,并将新用户设置值，将密码密文存储到数据库中
        User user = new User();

        user.setUserAccount(userAccount);
        user.setUserPassword(safetyPassword);
        user.setPlanetCode(planetCode);
        boolean save = this.save(user);
        //如果save为false
        if (!save) {
            throw new BaseException(ErrorCode.SYSTEM_ERROR, "插入失败");
        }
        return user.getId();
    }

    /**
     * 用户登录
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request
     * @return
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //非空判断
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
        //校验用户账号含有特殊字符
        boolean result = verifyUserAccount(userAccount);
        if (!result) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "账号含有特殊字符");
        }
        //从数据库中查询用户，校验账号和密码与存入数据库中的密文密码是否相等
        String safetyPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        userQueryWrapper.eq("userPassword", safetyPassword);
        User user = this.getOne(userQueryWrapper);
        if (user == null) {
            throw new BaseException(ErrorCode.SYSTEM_ERROR, "未查到该用户");
        }
        //对用户信息进行脱敏
        User safetyUser = getSafetyUser(user);

        //将用户信息存储到session中
        request.getSession().setAttribute(USER_LOGIN_STATUS, safetyUser);

        //返回脱敏后的用户信息
        return safetyUser;
    }

    /**
     * 用户注销
     * @param request
     * @return
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        request.getSession().removeAttribute(USER_LOGIN_STATUS);
        return true;
    }

    /**
     * 根据用户名查询用户
     * @param username
     * @param request
     * @return
     */
    @Override
    public User queryUserByName(String username, HttpServletRequest request) {
        //非空校验
        if (StringUtils.isBlank(username)) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        //用户长度校验
        if (username.length() < 4) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        //是否为管理员校验
        boolean admin = isAdmin(request);
        if (!admin) {
            throw new BaseException(ErrorCode.NO_AUTH, "该用户不是管理员");
        }
        //是管理员根据用户名称进行查询用户信息
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username);
        User user = this.getOne(userQueryWrapper);
        if (user == null) {
            throw new BaseException(ErrorCode.SYSTEM_ERROR, "未查到该用户");
        }
        //对用户信息进行脱敏,返回脱敏后的用户信息
        return getSafetyUser(user);
    }

    /**
     * 获取当前用户
     * @param request
     * @return
     */
    @Override
    public User getCurrentUser(HttpServletRequest request) {
        if (request == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        //从登录态中获取登录用户信息
        User user = getUser(request);
        if (user == null) {
            throw new BaseException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        //返回脱敏后的用户信息
        return getSafetyUser(user);
    }

    /**
     * 获取用户列表
     * @param request
     * @return
     */
    @Override
    public List<User> getUserList(HttpServletRequest request) {
        if (request == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        User user = getUser(request);
        if (user == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        //判断用户是否为管理员
        boolean admin = isAdmin(request);
        if (!admin) {
            throw new BaseException(ErrorCode.NO_AUTH, "该用户不是管理员");
        }
        //直接查询用户列表
        List<User> userList = this.list();
        //返回脱敏后的用户信息列表
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 根据用户id删除用户
     * @param id  要删除的用户id
     * @param request
     * @return
     */
    @Override
    public boolean deleteUserById(long id, HttpServletRequest request) {
        //从登录态中取出用户信息
        User user = getUser(request);
        if (user == null) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        //校验用户是否为管理员
        boolean admin = isAdmin(request);
        if (!admin) {
            throw new BaseException(ErrorCode.NO_AUTH, "该用户不是管理员");
        }
        boolean result = this.removeById(id);
        if (!result) {
            throw new BaseException(ErrorCode.SYSTEM_ERROR, "删除用户失败");
        }
        return true;
    }

    /**
     * 根据标签查询用户，只要用户有其中一个标签就可以(sql版）
     *
     * @param tagList
     * @return
     */
    @Override
    public List<User> selectUserByTagsBySQL(List<String> tagList) {
        //对tagList进行非空判断
        if (CollectionUtils.isEmpty(tagList)) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        //遍历标签列表，将用户的所有标签添加到查询条件中
        for (String tag : tagList) {
            userQueryWrapper = userQueryWrapper.like("tag", tag);
        }
        //根据查询条件查询数据
        List<User> userList = userMapper.selectList(userQueryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 根据标签搜索用户，用户必须有传入的所有标签才可以查询到(内存版)
     * @param tagList
     * @return
     */
    @Override
    @Deprecated
    public List<User> selectUserByTagsByMemory(List<String> tagList) {
        //非空判断
        if (CollectionUtils.isEmpty(tagList)) {
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        //查询出来所有用户
        List<User> userList = userMapper.selectList(userQueryWrapper);
        //由于标签是json数据,将json标签序列化成String类型
        Gson gson = new Gson();
        //将userList的所有标签查询出来
        return userList.stream().filter(user -> {
            String tags = user.getTags();
            //标签非空判断
            if (StringUtils.isNotBlank(tags)) {
                return false;
            }
            //将json标签转换成字符串类型
            Set<String> tagStr = gson.fromJson(tags, new TypeToken<Set<String>>() {
            }.getType());
            //判断用户的标签是否包含在要搜索的标签中，必须全部包含才能搜索到
            for (String tag : tagStr) {
                if (!tagList.contains(tag)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 修改用户信息
     * @param request
     * @param user
     * @return
     */
    @Override
    public int updateUserInfo(User user,HttpServletRequest request) {
        if (user==null){
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        //判断用户是否登录
        if (request==null){
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        Object obj = request.getSession().getAttribute(USER_LOGIN_STATUS);
        User loginUser = (User) obj;
        if (loginUser==null){
            throw new BaseException(ErrorCode.NOT_LOGIN);
        }

        //判断登录的用户是否是本人,登录用户是否为管理员
        Long userId = user.getId();
        Long loginUserId = loginUser.getId();
        if (!loginUserId.equals(userId) && !isAdmin(request)){
            throw new BaseException(ErrorCode.NO_AUTH);
        }
        //todo 补充校验 如果用户没有更新任何信息，那么就报错
        //如何进行没有修改信息的判断?判断要修改的字段前后的值是否相等
        //将User的值赋给UserDTO，比较修改前后user是否和userDTO相等
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user,userDTO, UserDTO.class);
        //修改用户信息
        int result = userMapper.updateById(user);
        //比较对象，如果userDTO与user相等，说明没有修改信息，直接报错
        if (userDTO.equals(user)){
            throw new BaseException(ErrorCode.PARAMS_ERROR);
        }
        if (result<0){
            throw new BaseException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }


    /**
     * 用户信息脱敏
     *
     * @param originUser
     * @return 脱敏后的用户信息
     */
    public User getSafetyUser(User originUser) {
        User user = new User();
        user.setId(originUser.getId());
        user.setUsername(originUser.getUsername());
        user.setUserAccount(originUser.getUserAccount());
        user.setAvatarUrl(originUser.getAvatarUrl());
        user.setGender(originUser.getGender());
        user.setCreateTime(originUser.getCreateTime());
        user.setUpdateTime(originUser.getUpdateTime());
        user.setIsDelete(originUser.getIsDelete());
        user.setUserRole(originUser.getUserRole());
        user.setPlanetCode(originUser.getPlanetCode());
        user.setEmail(originUser.getEmail());
        user.setPhone(originUser.getPhone());
        user.setTags(originUser.getTags());
        user.setProfile(originUser.getProfile());
        return user;
    }

    /**
     * 校验用户是否为管理员
     *
     * @param request
     * @return
     */
    public boolean isAdmin(HttpServletRequest request) {
        User user = getUser(request);
        return user.getUserRole().equals(ADMIN_AUTH);
    }


    /**
     * 校验用户账号是否含有非法字符
     *
     * @param userAccount
     * @return
     */
    public boolean verifyUserAccount(String userAccount) {
        String regEx = "[\\u00A0\\s\"`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern compile = Pattern.compile(regEx);
        Matcher matcher = compile.matcher(userAccount);
        //如果find找到成功之后，返回false
        if (matcher.find()) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "用户账号含有特殊字符");
        }
        return true;
    }

    /**
     * 从登录态中获取用户信息
     *
     * @param request
     * @return
     */
    private User getUser(HttpServletRequest request) {
        //从登录态中获取用户信息
        Object obj = request.getSession().getAttribute(USER_LOGIN_STATUS);
        User user = (User) obj;
        return user;
    }
}




