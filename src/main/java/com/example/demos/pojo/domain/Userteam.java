package com.example.demos.pojo.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName userteam
 */
@TableName(value ="userteam")
@Data
public class Userteam implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 队伍id
     */
    private Long teamId;

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
     * 加入时间
     */
    private Date joinTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}