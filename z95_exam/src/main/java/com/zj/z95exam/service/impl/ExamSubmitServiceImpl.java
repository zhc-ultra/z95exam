package com.zj.z95exam.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.z95exam.common.ErrorCode;
import com.zj.z95exam.exception.BusinessException;
import com.zj.z95exam.exception.ThrowUtils;
import com.zj.z95exam.model.dto.examsubmit.ExamSubmitAddRequest;
import com.zj.z95exam.model.dto.examsubmit.ExamSubmitQueryRequest;
import com.zj.z95exam.model.entity.*;
import com.zj.z95exam.model.entity.question.Answer;
import com.zj.z95exam.model.entity.question.QuestionContent;
import com.zj.z95exam.model.vo.ExamSubmitVO;
import com.zj.z95exam.model.vo.QuestionVO;
import com.zj.z95exam.service.CourseService;
import com.zj.z95exam.service.ExamSubmitService;
import com.zj.z95exam.mapper.ExamSubmitMapper;
import com.zj.z95exam.service.QuestionService;
import com.zj.z95exam.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zj
 * @description 针对表【exam_submit(考试提交)】的数据库操作Service实现
 * @createDate 2024-06-02 07:24:25
 */
@Service
public class ExamSubmitServiceImpl extends ServiceImpl<ExamSubmitMapper, ExamSubmit>
        implements ExamSubmitService {

    @Resource
    QuestionService questionService;

    @Resource
    UserService userService;

    @Resource
    CourseService courseService;

    @Override
    public ExamSubmitVO queryExamSubmitById(long examSubmitId, User student, Exam exam, Course course) {
        ExamSubmit examSubmit = getById(examSubmitId);

        QueryWrapper<ExamSubmit> examSubmitQueryWrapper = new QueryWrapper<>();
        examSubmitQueryWrapper
                .eq("userId", student.getId())
                .eq("examId", exam.getId());

        ExamSubmitVO examSubmitVO = new ExamSubmitVO();
        BeanUtils.copyProperties(examSubmit, examSubmitVO);

        List<String> records = JSONUtil.toList(examSubmit.getRecords(), String.class);
        List<QuestionVO> questionVOS = new ArrayList<>(records.size());
        for (String ans : records) {
            QuestionVO questionVO = new QuestionVO();
            Answer answer = JSONUtil.toBean(ans, Answer.class);
            Long questionId = answer.getQuestionId();
            Question question = questionService.getById(questionId);
            QuestionContent questionContent = JSONUtil.toBean(question.getContent(), QuestionContent.class);
            questionVO.setChoiceA(questionContent.getOptions()[0]);
            questionVO.setChoiceB(questionContent.getOptions()[1]);
            questionVO.setChoiceC(questionContent.getOptions()[2]);
            questionVO.setChoiceD(questionContent.getOptions()[3]);
            int index = questionContent.getAnswerIndex()[0];
            switch (index) {
                case 0:
                    questionVO.setAnswer(new String[]{"A"});
                    break;
                case 1:
                    questionVO.setAnswer(new String[]{"B"});
                    break;
                case 2:
                    questionVO.setAnswer(new String[]{"C"});
                    break;
                case 3:
                    questionVO.setAnswer(new String[]{"D"});
                    break;
                default:
                    break;
            }

            BeanUtils.copyProperties(question, questionVO);
            questionVO.setUserAnswer(answer.getAnswer());
            questionVOS.add(questionVO);
        }

        examSubmitVO.setStudentName(student.getUserName());
        examSubmitVO.setQuestions(questionVOS);

        long start = exam.getExamDate().getTime();
        long end = start + exam.getExamTime() * (60000L);
        long now = System.currentTimeMillis();
        // 根据考试开始时间和考试时长，设置考试状态
        if (now < start) {
            examSubmitVO.setState("考试未开始");
        } else if (now > end) {
            examSubmitVO.setState("考试已过期");
        } else {
            examSubmitVO.setState("考试中");
        }

        examSubmitVO.setExamDate(exam.getExamDate());
        examSubmitVO.setExamTime(exam.getExamTime());
        examSubmitVO.setStudentName(student.getUserName());
        examSubmitVO.setTitle(exam.getTitle());
        examSubmitVO.setCourseName(course.getName());
        ThrowUtils.throwIf(examSubmitVO == null, ErrorCode.NOT_FOUND_ERROR);
        return examSubmitVO;
    }

    @Override
    public Page<ExamSubmit> queryExamSubmitByPage(ExamSubmitQueryRequest examQueryRequest, User teacher) {
        // 创建者的 id
        if (teacher == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long classId = teacher.getClassId();
        long current = examQueryRequest.getCurrent();
        long size = examQueryRequest.getPageSize();
        // 获取所有提交记录
        List<ExamSubmit> examSubmitList = list();
        List<ExamSubmit> returnList = new ArrayList<>();
        for (ExamSubmit examSubmit : examSubmitList) {
            // 学生 id
            Long studentId = examSubmit.getUserId();
            User student = userService.getById(studentId);
            if (student.getClassId().equals(classId)) {
                // 是该老师的学生
                returnList.add(examSubmit);
            }
        }

        Page<ExamSubmit> questionPage = page(new Page<>(current, size));
        questionPage.setRecords(returnList);

        return questionPage;
    }

    @Override
    public boolean submitExam(ExamSubmitAddRequest examSubmitAddRequest, Exam exam, User student) {
        Long userId = student.getId();
        Long examId = examSubmitAddRequest.getExamId();
        // 获取考试
        if (exam == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取所有题目
        String content = exam.getContent();
        List<Question> questionList = JSONUtil.toList(content, Question.class);
        Integer finalScore = 0;// 考生得分
        // 获取学生答案
        Answer[] answer = examSubmitAddRequest.getAnswers();
        for (Answer ans : answer) {
            Long questionId = ans.getQuestionId();
            // 获取当前题目
            Question question = questionService.getById(questionId);
            // 获取正确答案
            String questionContentStr = question.getContent();
            QuestionContent questionContent = JSONUtil.toBean(questionContentStr, QuestionContent.class);
            Integer correctAnswerIndex = questionContent.getAnswerIndex()[0];

            String userAnswer = ans.getAnswer();
            Integer userAnswerIndex = null;
            if (userAnswer.equals("A")) {
                userAnswerIndex = 0;
            } else if (userAnswer.equals("B")) {
                userAnswerIndex = 1;
            } else if (userAnswer.equals("C")) {
                userAnswerIndex = 2;
            } else if (userAnswer.equals("D")) {
                userAnswerIndex = 3;
            }
            if (userAnswerIndex != null && userAnswerIndex.equals(correctAnswerIndex)) {
                finalScore += question.getScore();
            }
        }

        String records = JSONUtil.toJsonStr(examSubmitAddRequest.getAnswers());

        // 将考生答题数据数据插入数据库
        ExamSubmit examSubmit = new ExamSubmit();
        examSubmit.setUserId(userId);
        examSubmit.setExamId(examId);
        examSubmit.setRecords(records);
        examSubmit.setScore(finalScore);
        // 插入数据库
        return save(examSubmit);
    }

    @Override
    public ExamSubmitVO queryExamSubmitById(ExamSubmitQueryRequest examSubmitQueryRequest, Exam exam, ExamSubmit examSubmit, User user) {
        Long id = examSubmitQueryRequest.getUserId();
        User student = userService.getById(id);

        QueryWrapper<ExamSubmit> examSubmitQueryWrapper = new QueryWrapper<>();
        examSubmitQueryWrapper
                .eq("userId", id)
                .eq("examId", exam.getId());

        ExamSubmitVO examSubmitVO = new ExamSubmitVO();
        BeanUtils.copyProperties(examSubmit, examSubmitVO);

        List<String> records = JSONUtil.toList(examSubmit.getRecords(), String.class);
        List<QuestionVO> questionVOS = new ArrayList<>(records.size());
        for (String ans : records) {
            QuestionVO questionVO = new QuestionVO();
            Answer answer = JSONUtil.toBean(ans, Answer.class);
            Long questionId = answer.getQuestionId();
            Question question = questionService.getById(questionId);
            QuestionContent questionContent = JSONUtil.toBean(question.getContent(), QuestionContent.class);
            questionVO.setChoiceA(questionContent.getOptions()[0]);
            questionVO.setChoiceB(questionContent.getOptions()[1]);
            questionVO.setChoiceC(questionContent.getOptions()[2]);
            questionVO.setChoiceD(questionContent.getOptions()[3]);
            int index = questionContent.getAnswerIndex()[0];
            switch (index) {
                case 0:
                    questionVO.setAnswer(new String[]{"A"});
                    break;
                case 1:
                    questionVO.setAnswer(new String[]{"B"});
                    break;
                case 2:
                    questionVO.setAnswer(new String[]{"C"});
                    break;
                case 3:
                    questionVO.setAnswer(new String[]{"D"});
                    break;
                default:
                    break;
            }

            BeanUtils.copyProperties(question, questionVO);
            questionVO.setUserAnswer(answer.getAnswer());
            questionVOS.add(questionVO);
        }

        Long courseId = exam.getCourseId();
        Course course = courseService.getById(courseId);
        examSubmitVO.setStudentName(student.getUserName());
        examSubmitVO.setQuestions(questionVOS);

        long start = exam.getExamDate().getTime();
        long end = start + exam.getExamTime() * (60000L);
        long now = System.currentTimeMillis();
        // 根据考试开始时间和考试时长，设置考试状态
        if (now < start) {
            examSubmitVO.setState("考试未开始");
        } else if (now > end) {
            examSubmitVO.setState("考试已过期");
        } else {
            examSubmitVO.setState("考试中");
        }
        Long teacherId = exam.getTeacherId();
        User teacher = userService.getById(teacherId);
        examSubmitVO.setTeacherName(teacher.getUserName());
        examSubmitVO.setExamDate(exam.getExamDate());
        examSubmitVO.setExamTime(exam.getExamTime());
        examSubmitVO.setStudentName(student.getUserName());
        examSubmitVO.setTitle(exam.getTitle());
        int score = examSubmit.getScore();
        examSubmitVO.setScore(score);
        examSubmitVO.setCourseName(course.getName());
        ThrowUtils.throwIf(examSubmitVO == null, ErrorCode.NOT_FOUND_ERROR);
        return examSubmitVO;
    }
}