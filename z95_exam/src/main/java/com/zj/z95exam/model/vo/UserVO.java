package com.zj.z95exam.model.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author zj
 * @description 用户视图（脱敏）
 * @date 2024/6/4 08:43
 **/
@Data
public class UserVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}