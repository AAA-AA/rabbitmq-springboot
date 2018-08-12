package com.github.rabbitmq.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author : hongqiangren.
 * @since: 2018/8/12 15:19
 */
@Data
public class UserDto {
    private Long id;
    private String nickName;
    private Date createTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
