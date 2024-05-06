package com.example.demos.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data

public class UserDTO implements Serializable {


    private static final long serialVersionUID = 8401283551882576379L;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 用户性别
     */
    private Integer gender;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 用户json标签列表
     */
    private String tags;

    /**
     * 用户介绍
     */
    private String profile;


}
