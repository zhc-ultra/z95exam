package com.zj.z95exam.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.z95exam.common.ErrorCode;
import com.zj.z95exam.exception.BusinessException;
import com.zj.z95exam.model.dto.exam.ExamAddRequest;
import com.zj.z95exam.model.dto.exam.ExamQueryRequest;
import com.zj.z95exam.model.dto.exam.ExamUpdateRequest;
import com.zj.z95exam.model.entity.*;
import com.zj.z95exam.model.entity.question.QuestionContent;
import com.zj.z95exam.model.enums.QuestionTypeEnum;
import com.zj.z95exam.model.vo.ExamVO;
import com.zj.z95exam.model.vo.QuestionVO;
import com.zj.z95exam.service.CourseService;
import com.zj.z95exam.service.ExamService;
import com.zj.z95exam.mapper.ExamMapper;
import com.zj.z95exam.service.ExamSubmitService;
import com.zj.z95exam.service.QuestionService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zj
 * @description 针对表【exam(考试)】的数据库操作Service实现
 * @createDate 2024-06-02 07:24:25
 */
@Service
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam>
        implements ExamService {
    // 业务层之间尽量减少相互的调用，减少耦合，并且可以有效避免循环依赖
    @Resource
    QuestionService questionService;

    @Resource
    CourseService courseService;

    @Resource
    ExamSubmitService examSubmitService;

    /**
     * 获取考试列表
     *
     * @param examQueryRequest 考试查询 dto
     * @param loginUser        登录用户(老师)
     * @return 考试列表
     */
    @Override
    public Page<Exam> listExam(ExamQueryRequest examQueryRequest, User loginUser) {
        // 获取登录用户
        examQueryRequest.setId(loginUser.getId());

        // 分页请求
        long current = examQueryRequest.getCurrent();
        long size = examQueryRequest.getPageSize();

        // 使用MyBatis-Plus 的查询条件构造器，拼接查询条件
        QueryWrapper<Exam> examQueryWrapper = new QueryWrapper<>();
        examQueryWrapper.eq("teacherId", loginUser.getId());

        // 封装查询条件
        return page(new Page<>(current, size), examQueryWrapper);
    }

    /**
     * 删除考试
     *
     * @param id 考试id
     * @return 删除成功与否
     */
    @Override
    public boolean deleteExam(long id) {
        // 直接调用MyBatis-Plus 提供的removeById() 方法删除考试
        return removeById(id);
    }

    /**
     * 更新考试信息
     *
     * @param examUpdateRequest 更新考试信息 dto
     * @return 更新成功与否
     */
    @Override
    public boolean updateExam(ExamUpdateRequest examUpdateRequest) {
        if (examUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long examId = examUpdateRequest.getId();
        if (examId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Exam exam = getById(examId);
        exam.setId(examId);

        // 获取需要更新的有效字段
        if (StrUtil.isNotBlank(examUpdateRequest.getTitle())) {
            exam.setTitle(examUpdateRequest.getTitle());
        }
        if (StrUtil.isNotBlank(examUpdateRequest.getContent())) {
            exam.setContent(examUpdateRequest.getContent());
        }
        if (StrUtil.isNotBlank(examUpdateRequest.getTags())) {
            exam.setTags(examUpdateRequest.getTags());
        }
        if (examUpdateRequest.getExamDate() != null) {
            exam.setExamDate(examUpdateRequest.getExamDate());
        }
        if (examUpdateRequest.getExamTime() != null && examUpdateRequest.getExamTime() != 0) {
            exam.setExamTime(examUpdateRequest.getExamTime());
        }
        // 更新考试
        return updateById(exam);
    }

    /**
     * 根据考试id查询考试
     *
     * @param id      考试 id
     * @param course  课程
     * @param teacher 老师
     * @return 考试视图
     */
    @Override
    public ExamVO queryExamById(long id, Course course, User teacher) {
        Exam exam = getById(id);
        ExamVO examVO = new ExamVO();
        // 拷贝考试信息到目标对象
        BeanUtils.copyProperties(exam, examVO);
        // 获取题目列表, 并转换为VO TODO 这里的题目列表是以JSON数组的形式存储在考试的content字段中
        List<Question> questionList = JSONUtil.toList(exam.getContent(), Question.class);
        List<QuestionVO> questionVOList = new ArrayList<>(questionList.size());
        // 遍历所有题目
        for (Question question : questionList) {
            QuestionVO questionVO = new QuestionVO();
            questionVO.setContent(JSONUtil.toBean(question.getContent(), QuestionContent.class));
            // 拷贝名称相同的字段的值到目标对象
            BeanUtils.copyProperties(question, questionVO);
            // 如果是单选题单选题
            if (questionVO.getType().equals(QuestionTypeEnum.SINGLE.getValue())) {
                QuestionContent questionContent = questionVO.getContent();
                questionVO.setTitle(question.getTitle());
                // 补充返回的参数，设置好前端需要显示的字段
                questionVO.setChoiceA(questionContent.getOptions()[0]);
                questionVO.setChoiceB(questionContent.getOptions()[1]);
                questionVO.setChoiceC(questionContent.getOptions()[2]);
                questionVO.setChoiceD(questionContent.getOptions()[3]);
                // 如果是老师，则显示答案
                if (teacher.getUserRole().equals("teacher")) {
                    int ansIndex = questionContent.getAnswerIndex()[0];
                    String answer = "";
                    switch (ansIndex) {
                        case 0:
                            answer = "A";
                            break;
                        case 1:
                            answer = "B";
                            break;
                        case 2:
                            answer = "C";
                            break;
                        case 3:
                            answer = "D";
                            break;
                        default:
                            break;
                    }
                    // 设置正确答案
                    questionVO.setAnswer(new String[]{answer});
                }
                // 数据脱敏，节省网络带宽
                questionVO.setContent(null);
                questionVOList.add(questionVO);
            }
        }
        examVO.setContent(null);
        examVO.setQuestions(questionVOList);
        examVO.setCourseName(course.getName());
        // 设置考试状态
        long start = examVO.getExamDate().getTime();
        // 1 分钟 = 60 秒 = 60 * 1000毫秒
        long end = start + examVO.getExamTime() * (60000L);
        long now = System.currentTimeMillis();
        // 根据考试开始时间和考试时长，设置考试状态
        if (now < start) {
            examVO.setState("考试未开始");
        } else if (now > end) {
            examVO.setState("考试已过期");
        } else {
            examVO.setState("考试中");
        }
        // 补充参数
        examVO.setTeacherName(teacher.getUserName());

        return examVO;
    }

    /**
     * 获取学生所有考试
     *
     * @param examQueryRequest 考试查询 dto
     * @param teacher          学生信息
     * @return 学生考试列表(状态为考试中)
     */
    @Override
    public Page<ExamVO> getStudentAllExam(ExamQueryRequest examQueryRequest, User teacher) {
        Long classId = teacher.getClassId();
        // 分页参数 要第几页 每一页多少条数据
        // n = (current - 1) * pageSize
        long current = examQueryRequest.getCurrent();
        long pageSize = examQueryRequest.getPageSize();

        // 获取该班级所有的考试
        // 拼接查询请求
        QueryWrapper<Exam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("classId", classId);

        // 获取分页后的考试列表
        Page<Exam> examPage = page(new Page<>(current, pageSize), queryWrapper);
        List<Exam> examList = examPage.getRecords();
        List<ExamVO> examVOList = new ArrayList<>(examList.size());
        // 处理考试列表
        for (int i = 0; i < examList.size(); i++) {
            ExamVO examVO = new ExamVO();
            // 拷贝同名字段的值
            BeanUtils.copyProperties(examList.get(i), examVO);
            Long courseId = examList.get(i).getCourseId();
            // 拼接查询请求
            QueryWrapper<Course> qw = new QueryWrapper<>();
            qw.eq("id", courseId);
            // 查询考试课程
            Course course = courseService.getOne(qw);
            // 补充参数
            examVO.setCourseName(course.getName());

            // 设置考试状态
            long start = examVO.getExamDate().getTime();
            long end = start + examVO.getExamTime() * (60000L);
            long now = System.currentTimeMillis();
            // 根据考试开始时间和考试时长，设置考试状态
            if (now < start) {
                examVO.setState("考试未开始");
            } else if (now > end) {
                examVO.setState("考试已过期");
            } else {
                Long userId = teacher.getId();
                Long examId = examList.get(i).getId();
                QueryWrapper<ExamSubmit> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1
                        .eq("userId", userId)
                        .eq("examId", examId);
                if (examSubmitService.getOne(queryWrapper1) == null) {
                    examVO.setState("考试中");
                } else {
                    examVO.setState("已完成考试");
                }
            }
            examVOList.add(examVO);
        }
        // 封装分页结果
        Page<ExamVO> examVOPage = new Page<>(examPage.getCurrent(), examPage.getSize(), examPage.getTotal());
        examVOPage.setRecords(examVOList);
        return examVOPage;
    }

    /**
     * 获取学生考试中心
     *
     * @param examQueryRequest 考试查询 dto
     * @param student          学生信息
     * @return 学生考试中心
     */
    @Override
    public Page<ExamVO> getStudentExamCenter(ExamQueryRequest examQueryRequest, User student) {
        Long classId = student.getClassId();
        // 分页查询请求
        long current = examQueryRequest.getCurrent();
        long pageSize = examQueryRequest.getPageSize();
        // 获取该班级所有的考试 拼接查询请求，相当与拼接sql where classId = #{classId}
        QueryWrapper<Exam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("classId", classId);

        // 获取分页后的考试列表
        Page<Exam> examPage = page(new Page<>(current, pageSize), queryWrapper);
        List<Exam> examList = examPage.getRecords();
        List<ExamVO> examVOList = new ArrayList<>(examList.size());
        // 处理考试列表
        for (int i = 0; i < examList.size(); i++) {
            ExamVO examVO = new ExamVO();
            // 拷贝同名字段的值
            BeanUtils.copyProperties(examList.get(i), examVO);
            Long courseId = examList.get(i).getCourseId();
            // 拼接查询请求
            QueryWrapper<Course> qw = new QueryWrapper<>();
            qw.eq("id", courseId);
            Course course = courseService.getOne(qw);
            examVO.setCourseName(course.getName());
            // 设置考试状态
            long start = examVO.getExamDate().getTime();
            long end = start + examVO.getExamTime() * (60000L);
            long now = System.currentTimeMillis();
            // 根据考试开始时间和考试时长，设置考试状态
            if (now < start) {
                examVO.setState("考试未开始");
            } else if (now > end) {
                examVO.setState("考试已过期");
            } else {
                examVO.setState("考试中");
                // 查询学生是否以及考试
                Long examId = examList.get(i).getId();
                Long studentId = student.getId();
                QueryWrapper<ExamSubmit> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1
                        .eq("userId", studentId)
                        .eq("examId", examId);
                if (examSubmitService.getOne(queryWrapper1) != null) {
                    examVO.setState("考试已提交");
                } else {
                    examVOList.add(examVO);
                }
            }
        }
        // 封装分页结果
        Page<ExamVO> examVOPage = new Page<>(examPage.getCurrent(), examPage.getSize(), examPage.getTotal());
        examVOPage.setRecords(examVOList);
        return examVOPage;
    }

    /**
     * 添加考试
     *
     * @param examAddRequest 考试添加 dto
     * @param teacher        老师信息
     * @return 添加考试 id
     */
    @Override
    public long addExam(ExamAddRequest examAddRequest, User teacher) {
        Exam exam = new Exam();
        exam.setTitle(examAddRequest.getTitle());
        // 获取题目id列表
        List<Integer> questionIds = examAddRequest.getContent();
        // 获取题目列表
        List<Question> questionList = questionService.listByIds(questionIds);
        // 设置添加的考试的信息
        String contentStr = JSONUtil.toJsonStr(questionList);
        exam.setContent(contentStr);
        exam.setExamDate(examAddRequest.getExamDate());
        exam.setExamTime(examAddRequest.getExamTime());
        exam.setCourseId(examAddRequest.getCourseId());
        exam.setClassId(examAddRequest.getClassId());
        exam.setTeacherId(teacher.getId());
        // 插入考试
        int result = getBaseMapper().insert(exam);
        if (result > 0) {
            return exam.getId();
        }
        return -1;
    }
}