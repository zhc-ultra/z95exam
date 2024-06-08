package com.zj.z95exam.model.dto.exam;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zj
 * @description 考试添加请求
 * @date 2024/6/8 17:44
 **/
@Data
public class ExamAddRequest implements Serializable {
    /**
     * 标题
     */
    private String title;

    /**
     * 内容(JSON str 题目 id  List<Integer> ids 列表)
     */
    private List<Integer> content;

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 考试班级
     */
    private Long classId;
    /**
     * 考试日期
     */
    private Date examDate;
    /**
     * 考试时长
     */
    private Integer examTime;

    private static final long serialVersionUID = 1L;
}