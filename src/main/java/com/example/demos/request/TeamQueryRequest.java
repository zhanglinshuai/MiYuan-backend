package com.example.demos.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class TeamQueryRequest implements Serializable {


    private static final long serialVersionUID = 21289014114693503L;
    private int id;

    private String name;

    private int maxNum;

    private int teamStatus;

    private Date expireTime;

    private String password;

    private String description;
}
