package com.zj.z95exam.model.dto.question;

import com.zj.z95exam.model.entity.question.QuestionContent;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zj
 * @description 题目更新请求
 * @date 2024/6/8 17:45
 **/
@Data
public class QuestionUpdateRequest implements Serializable {
    private Long id;
    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private QuestionContent content;

    /**
     * 考试类型（单选题、多选题、判断题）
     */
    private Integer type;

    /**
     * 题目答案
     */
    private String[] answer;

    /**
     * 创建用户 id
     */
    private Long userId;

    private String courseName;

    /**
     * 是否删除
     */
    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}