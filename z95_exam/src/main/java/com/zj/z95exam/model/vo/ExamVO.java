package com.zj.z95exam.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author zj
 * @description 考试视图对象
 * @date 2024/6/4 08:43
 **/
@Data
public class ExamVO {

    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    private List<QuestionVO> questions;

    /**
     * 创建老师 id
     */
    private Long teacherId;

    /**
     * 课程id
     */
    private Long courseId;

    private String state;

    private String courseName;


    private String teacherName;
    /**
     * 考试日期
     */
    private Date examDate;
    /**
     * 考试时长
     */
    private Integer examTime;


}
