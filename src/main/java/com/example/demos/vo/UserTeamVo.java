package com.example.demos.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class UserTeamVo implements Serializable {
    /**
     * 队伍id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 密码
     */
    private String password;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 用户过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * 队伍状态 0-公开，1-私有，2-加密
     */
    private Integer teamStatus;
    /**
     * 创建人的用户信息
     */
    private UserVo createUser;
    /**
     * 已经加入队伍的人数
     */
    private Integer HasJoinNum;
    /**
     * 是否已经加入队伍
     */
    private boolean HasJoin = false;
}
