package com.zj.z95exam.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zj
 * @description 题目类型枚举，目前只支持单选题
 * @date 2024/6/8 17:49
 **/
public enum QuestionTypeEnum {
    SINGLE("单选", 0),
    MULTIPLE("多选", 1),
    JUDGE("判断", 2),
    FILL("填空", 3),
    SUBJECTIVE("主观", 4);

    private final String text;
    private final Integer value;

    QuestionTypeEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     */
    public static QuestionTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (QuestionTypeEnum anEnum : QuestionTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
