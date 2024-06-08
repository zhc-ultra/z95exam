package com.zj.z95exam.model.dto.exam;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * @author zj
 * @description 考试更新请求
 * @date 2024/6/8 17:45
 **/
@Data
public class ExamUpdateRequest implements Serializable {
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
     * 标签列表
     */
    private String tags;
    /**
     * 考试日期
     */
    private Date examDate;
    /**
     * 课程id
     */
    private Long courseId;
    /**
     * 班级 id
     */
    private Long classId;
    /**
     * 考试时长
     */
    private Integer examTime;

    private static final long serialVersionUID = 1L;
}