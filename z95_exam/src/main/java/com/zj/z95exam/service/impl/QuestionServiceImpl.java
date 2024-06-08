package com.zj.z95exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.z95exam.common.ErrorCode;
import com.zj.z95exam.exception.BusinessException;
import com.zj.z95exam.model.dto.question.QuestionAddRequest;
import com.zj.z95exam.model.dto.question.QuestionQueryRequest;
import com.zj.z95exam.model.dto.question.QuestionUpdateRequest;
import com.zj.z95exam.model.entity.Course;
import com.zj.z95exam.model.entity.Question;
import com.zj.z95exam.model.entity.User;
import com.zj.z95exam.model.entity.question.QuestionContent;
import com.zj.z95exam.model.vo.QuestionVO;
import com.zj.z95exam.service.CourseService;
import com.zj.z95exam.service.QuestionService;
import com.zj.z95exam.mapper.QuestionMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zj
 * @description 针对表【question(题目)】的数据库操作Service实现
 * @createDate 2024-06-02 07:24:25
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Resource
    private CourseService courseService;

    @Override
    public boolean addQuestion(QuestionAddRequest questionAddRequest, User teacher) {
        // 题目创建者
        Long userId = teacher.getId();
        String courseName = questionAddRequest.getCourseName();

        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", courseName);

        Course course = courseService.getOne(queryWrapper);
        // 课程不存在
        if (course == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 题目答案
        String[] answer = questionAddRequest.getAnswer();

        // 题目选项（题目内容）
        QuestionContent content = questionAddRequest.getContent();

        // 题目类型
        Integer type = questionAddRequest.getType();
        String[] options = content.getOptions();
        List<Integer> answersIndex = new ArrayList<>();
        for (int i = 0; i < options.length; i++) {
            for (String s : answer) {
                if (options[i].equals(s)) {
                    answersIndex.add(i);
                }
            }
        }
        Integer[] answerIndexArray = new Integer[answersIndex.size()];

        int i = 0;
        for (Integer answerIndex : answersIndex) {
            answerIndexArray[i++] = answerIndex;
        }
        content.setAnswerIndex(answerIndexArray);
        // 题干
        String title = questionAddRequest.getTitle();
        Question question = new Question();
        question.setTitle(title);
        question.setContent(JSONUtil.toJsonStr(content));
        question.setType(type);
        question.setAnswer(JSONUtil.toJsonStr(answer));
        question.setUserId(userId);
        question.setCourseId(course.getId());

        if (save(question)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteQuestion(long id) {
        return removeById(id);
    }

    @Override
    public boolean updateQuestion(QuestionUpdateRequest questionUpdateRequest) {
        Question question = getById(questionUpdateRequest.getId());

        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        if (StrUtil.isNotBlank(questionUpdateRequest.getTitle())) {
            question.setTitle(questionUpdateRequest.getTitle());
        }

        if (questionUpdateRequest.getContent() != null) {
            question.setContent(JSONUtil.toJsonStr(questionUpdateRequest.getContent()));
        }
        if (StrUtil.isNotBlank(questionUpdateRequest.getCourseName())) {
            QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name", questionUpdateRequest.getCourseName());
            Course course = courseService.getOne(queryWrapper);
            question.setCourseId(course.getId());
        }

        if (questionUpdateRequest.getType() != null) {
            question.setType(questionUpdateRequest.getType());
        }
        List<String> ans = new ArrayList<>();
        for (String a : questionUpdateRequest.getAnswer()) {
            if (!a.equals("")) {
                ans.add(a);
            }
        }
        String[] answer = new String[ans.size()];
        int i = 0;
        for (String a : ans) {
            answer[i++] = a;
        }
        if (questionUpdateRequest.getAnswer() != null) {
            question.setAnswer(JSONUtil.toJsonStr(answer));
        }

        return updateById(question);
    }

    @Override
    public QuestionVO queryQuestionById(QuestionQueryRequest questionQueryRequest) {
        Question question = getById(questionQueryRequest.getId());
        return getQuestionVO(question);
    }

    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollUtil.isEmpty(questionList)) {
            return questionVOPage;
        }

        List<QuestionVO> questionVOList = new ArrayList<>();
        for (Question question : questionList) {
            questionVOList.add(getQuestionVO(question));
        }

        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

    @Override
    public Page<QuestionVO> queryQuestionVOPage(QuestionQueryRequest questionQueryRequest, User teacher,HttpServletRequest request) {
        if (teacher == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", teacher.getId());

        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();

        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("userId", teacher.getId());
        Page<Question> questionPage = page(new Page<>(current, size), questionQueryWrapper);
        return getQuestionVOPage(questionPage, request);
    }


    public QuestionVO getQuestionVO(Question question) {
        QuestionVO questionVO = new QuestionVO();
        questionVO.setId(question.getId());
        Long courseId = question.getCourseId();
        if (courseId != null) {
            QueryWrapper<Course> courseQueryWrapper = new QueryWrapper<>();
            courseQueryWrapper.eq("id", courseId);
            Course course = courseService.getOne(courseQueryWrapper);
            questionVO.setCourseName(course.getName());
        }
        questionVO.setTitle(question.getTitle());
        questionVO.setContent(JSONUtil.toBean(question.getContent(), QuestionContent.class));
        questionVO.setType(question.getType());
        questionVO.setAnswer(JSONUtil.parseArray(question.getAnswer()).toArray(new String[0]));
        return questionVO;
    }
}