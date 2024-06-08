package com.zj.z95exam.model.entity.question;

import lombok.Data;

/**
 * @author zj
 * @description
 * @date 2024/6/3 07:06
 **/
@Data
public class QuestionContent {
    // 0 -> A or false or text
    // 1 -> B or true
    // 2 -> C or text
    // 3 -> D or text
    // 4 -> E or text
    // ...
    private String[] options;
    private Integer[] answerIndex;
}
