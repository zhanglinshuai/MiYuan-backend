package com.example.demos.pojo.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName team
 */
@TableName(value ="team")
@Data
public class Team implements Serializable {
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
     *  是否删除 0-不删除 1-删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 队伍状态 0-公开，1-私有，2-加密
     */
    private Integer teamStatus;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}