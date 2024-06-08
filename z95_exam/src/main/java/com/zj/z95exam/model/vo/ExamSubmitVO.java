package com.zj.z95exam.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author zj
 * @description 考试提交视图对象
 * @date 2024/6/7 08:43
 **/
@Data
public class ExamSubmitVO {
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 处理之后返回给前端的题目列表
     */
    private List<QuestionVO> questions;

    /**
     * 创建老师 id
     */
    private Long teacherId;

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 考试状态
     */
    private String state;

    /**
     * 考试课程
     */
    private String courseName;


    /**
     * 出卷教师姓名
     */
    private String teacherName;

    /**
     * 答题学生姓名
     */
    private String studentName;

    /**
     * 学生得分
     */
    private Integer score;
    /**
     * 考试日期
     */
    private Date examDate;
    /**
     * 考试时长
     */
    private Integer examTime;
}
