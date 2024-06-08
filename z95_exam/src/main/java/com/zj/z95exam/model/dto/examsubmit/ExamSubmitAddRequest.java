package com.zj.z95exam.model.dto.examsubmit;

import com.zj.z95exam.model.entity.question.Answer;
import lombok.Data;

/**
 * @author zj
 * @description 提交考试添加请求
 * @date 2024/6/4 14:59
 **/
@Data
public class ExamSubmitAddRequest {
    private Long examId;
    private Answer[] answers;
}
