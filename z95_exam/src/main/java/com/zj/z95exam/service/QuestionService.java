package com.zj.z95exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zj.z95exam.model.dto.question.QuestionAddRequest;
import com.zj.z95exam.model.dto.question.QuestionQueryRequest;
import com.zj.z95exam.model.dto.question.QuestionUpdateRequest;
import com.zj.z95exam.model.entity.Question;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zj.z95exam.model.entity.User;
import com.zj.z95exam.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zj
 * @description 针对表【question(题目)】的数据库操作Service
 * @createDate 2024-06-02 07:24:25
 */
public interface QuestionService extends IService<Question> {

    /**
     * 添加题目
     *
     * @param questionAddRequest dto 添加题目请求
     * @param teacher            教师信息
     * @return 是否添加成功
     */
    boolean addQuestion(QuestionAddRequest questionAddRequest, User teacher);

    /**
     * 删除题目
     *
     * @param id 题目id
     * @return 是否删除成功
     */
    boolean deleteQuestion(long id);

    /**
     * 更新题目
     *
     * @param questionUpdateRequest dto 更新题目请求
     * @return 是否更新成功
     */
    boolean updateQuestion(QuestionUpdateRequest questionUpdateRequest);

    /**
     * 根据id查询题目
     *
     * @param questionQueryRequest dto 查询题目请求
     * @return 题目
     */
    QuestionVO queryQuestionById(QuestionQueryRequest questionQueryRequest);

    /**
     * 获取题目封装类
     *
     * @param question 题目
     * @return 题目封装类
     */
    QuestionVO getQuestionVO(Question question);

    /**
     * 分页获取题目封装类
     *
     * @param questionPage 分页查询题目
     * @param request      请求
     * @return 分页题目封装类
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    /**
     * 分页查询题目
     *
     * @param questionQueryRequest dto 查询题目请求
     * @param teacher              教师信息
     * @param request              请求
     * @return 分页题目
     */
    Page<QuestionVO> queryQuestionVOPage(QuestionQueryRequest questionQueryRequest, User teacher, HttpServletRequest request);
}
