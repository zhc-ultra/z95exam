package com.zj.z95exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zj.z95exam.model.dto.examsubmit.ExamSubmitAddRequest;
import com.zj.z95exam.model.dto.examsubmit.ExamSubmitQueryRequest;
import com.zj.z95exam.model.entity.Course;
import com.zj.z95exam.model.entity.Exam;
import com.zj.z95exam.model.entity.ExamSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zj.z95exam.model.entity.User;
import com.zj.z95exam.model.vo.ExamSubmitVO;

/**
 * @author zj
 * @description 针对表【exam_submit(考试提交)】的数据库操作Service
 * @createDate 2024-06-02 07:24:25
 */
public interface ExamSubmitService extends IService<ExamSubmit> {
    /**
     * 通过id查询考试提交记录
     *
     * @param examSubmitId 考试提交id
     * @param student      提交考试的学生信息
     * @param exam         考试信息
     * @param course       考试课程
     * @return 考试提交记录的VO对象
     */
    ExamSubmitVO queryExamSubmitById(long examSubmitId, User student, Exam exam, Course course);

    /**
     * 通过分页查询考试提交记录
     *
     * @param examQueryRequest 考试提交查询请求
     * @param teacher          教师信息
     * @return 考试提交记录的分页对象
     */
    Page<ExamSubmit> queryExamSubmitByPage(ExamSubmitQueryRequest examQueryRequest, User teacher);

    /**
     * 提交考试信息(学生答题完毕,交卷)
     *
     * @param examSubmitAddRequest 考试提交添加请求
     * @param exam                 考试信息
     * @param student              学生信息
     * @return 是否提交成功
     */
    boolean submitExam(ExamSubmitAddRequest examSubmitAddRequest, Exam exam, User student);

    /**
     * 通过id查询考试提交记录
     *
     * @param examSubmitQueryRequest 考试提交查询请求
     * @param exam                   考试信息
     * @param examSubmit             考试提交信息
     * @param user                   用户信息
     * @return 考试提交记录的VO对象
     */
    ExamSubmitVO queryExamSubmitById(ExamSubmitQueryRequest examSubmitQueryRequest, Exam exam, ExamSubmit examSubmit, User user);
}
