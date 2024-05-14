package com.example.demos.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Data
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = -1599560813806739301L;

    /**
     * 队伍id
     */
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
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
     * 队伍状态 0-公开，1-私有，2-加密
     */
    private Integer teamStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamUpdateRequest that = (TeamUpdateRequest) o;
        return Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(name, that.name) && Objects.equals(password, that.password) && Objects.equals(description, that.description) && Objects.equals(maxNum, that.maxNum) && Objects.equals(expireTime, that.expireTime) && Objects.equals(teamStatus, that.teamStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, name, password, description, maxNum, expireTime, teamStatus);
    }
}
