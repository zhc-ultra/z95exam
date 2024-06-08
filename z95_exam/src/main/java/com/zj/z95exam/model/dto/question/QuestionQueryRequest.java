package com.zj.z95exam.model.dto.question;

import com.baomidou.mybatisplus.annotation.TableField;
import com.zj.z95exam.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author zj
 * @description 题目查询请求
 * @date 2024/6/8 17:45
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    private Long courseId;

    private Long userId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}