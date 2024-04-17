package com.example.demos.pojo.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 
     */
    private Long id;

    /**
     * 
     */
    private String username;

    /**
     * 
     */
    private String userAccount;

    /**
     * 
     */
    private String userPassword;

    /**
     * 
     */
    private String avatarUrl;

    /**
     * 
     */
    private Integer gender;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 
     */
    private Integer userRole;

    /**
     * 
     */
    private String planetCode;

    /**
     * 
     */
    private String email;

    /**
     * 
     */
    private String phone;

    /**
     * 
     */
    private String tags;

    /**
     * 用户介绍
     */
    private String profile;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}