package com.zj.z95exam.model.dto.question;

import com.zj.z95exam.model.entity.question.QuestionContent;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zj
 * @description 题目添加请求
 * @date 2024/6/8 17:45
 **/
@Data
public class QuestionAddRequest implements Serializable {
    /**
     * 课程名称 关联课程表中查询
     */
    private String courseName;

    /**
     * 题干
     */
    private String title;

    /**
     * 题目内容 (多题型，使用JSON数组进行存储 QuestionContent.class)
     */
    private QuestionContent content;

    /**
     * 0 -> 单选
     * 1 -> 多选
     * 2 -> 判断
     * 3 -> 填空
     * 4 -> 主观
     * 考试类型
     */
    private Integer type;

    /**
     * 题目答案
     */
    private String[] answer;

    private static final long serialVersionUID = 1L;
}