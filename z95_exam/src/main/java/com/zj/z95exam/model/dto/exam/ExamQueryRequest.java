package com.zj.z95exam.model.dto.exam;

import com.zj.z95exam.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * @author zj
 * @description 考试查询
 * @date 2024/6/8 17:44
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ExamQueryRequest extends PageRequest implements Serializable {
    /**
     *
     */
    private Long id;

    /**
     * 标题
     */
    private String title;
    /**
     * 课程id
     */
    private Long courseId;
    /**
     * 创建老师 id
     */
    private Long teacherId;

    private static final long serialVersionUID = 1L;
}