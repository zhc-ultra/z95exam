package com.zj.z95exam.model.vo;

import com.zj.z95exam.common.ErrorCode;
import com.zj.z95exam.exception.BusinessException;
import com.zj.z95exam.model.entity.question.QuestionContent;
import com.zj.z95exam.model.enums.QuestionTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zj
 * @description 题目视图枚举
 * @date 2024/6/4 08:43
 **/
@Data
public class QuestionVO implements Serializable {
    /**
     * 题目 id
     */
    private Long id;

    private String courseName;

    /**
     * 题目标题
     */
    private String title;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;

    private String userAnswer;


    /**
     * 题目内容
     */
    private QuestionContent content;

    /**
     * 0 -> 单选
     * 1 -> 多选
     * 2 -> 判断
     * 考试类型（单选题、多选题、判断题）
     */
    private Integer type;

    private String typeCN;

    private Integer score;

    public void setType(Integer type) {
        this.type = type;
        if (type.equals(QuestionTypeEnum.SINGLE.getValue())) {
            typeCN = QuestionTypeEnum.SINGLE.getText();
        } else if (type.equals(QuestionTypeEnum.MULTIPLE.getValue())) {
            typeCN = QuestionTypeEnum.MULTIPLE.getText();
        } else if (type.equals(QuestionTypeEnum.JUDGE.getValue())) {
            typeCN = QuestionTypeEnum.JUDGE.getText();
        } else if (type.equals(QuestionTypeEnum.FILL.getValue())) {
            typeCN = QuestionTypeEnum.FILL.getText();
        } else if (type.equals(QuestionTypeEnum.SUBJECTIVE.getValue())) {
            typeCN = QuestionTypeEnum.SUBJECTIVE.getText();
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
    }

    /**
     * 题目答案
     */
    private String[] answer;

    private static final long serialVersionUID = 1L;
}