package com.zj.z95exam.model.dto.examsubmit;

import com.baomidou.mybatisplus.annotation.TableField;
import com.zj.z95exam.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zj
 * @description 提交考试查询请求
 * @date 2024/6/4 14:59
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ExamSubmitQueryRequest extends PageRequest implements Serializable {
    private Long id;

    /**
     * 考试学生 id
     */
    private Long userId;

    /**
     * 考试 id
     */
    private Long examId;

    /**
     * 分数
     */
    private Integer score;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}