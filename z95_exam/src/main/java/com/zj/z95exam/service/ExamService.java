package com.zj.z95exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zj.z95exam.model.dto.exam.ExamAddRequest;
import com.zj.z95exam.model.dto.exam.ExamQueryRequest;
import com.zj.z95exam.model.dto.exam.ExamUpdateRequest;
import com.zj.z95exam.model.entity.Course;
import com.zj.z95exam.model.entity.Exam;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zj.z95exam.model.entity.User;
import com.zj.z95exam.model.vo.ExamVO;

/**
 * @author zj
 * @description 针对表【exam(考试)】的数据库操作Service
 * @createDate 2024-06-02 07:24:25
 */
public interface ExamService extends IService<Exam> {

    /**
     * 查询班级考试列表
     *
     * @param request   考试查询 dto
     * @param loginUser 登录用户(老师)
     * @return 分页考试列表
     */
    Page<Exam> listExam(ExamQueryRequest request, User loginUser);

    /**
     * 添加考试
     *
     * @param examAddRequest 考试添加 dto
     * @param teacher        老师信息
     * @return 如果添加成功则返回添加的考试id，否则返回-1
     */
    long addExam(ExamAddRequest examAddRequest, User teacher);

    /**
     * 删除班级考试
     *
     * @param id 考试id
     * @return 是否删除成功
     */

    boolean deleteExam(long id);

    /**
     * 更新考试信息
     *
     * @param examUpdateRequest 更新考试信息 dto
     * @return 是否更新成功
     */
    boolean updateExam(ExamUpdateRequest examUpdateRequest);

    /**
     * 查询考试
     *
     * @param id      考试 id
     * @param course  课程
     * @param teacher 老师
     * @return 考试VO类(进行数据的脱敏和补充)
     */
    ExamVO queryExamById(long id, Course course, User teacher);

    /**
     * 查询某个学生考试列表(所有状态的考试)
     *
     * @param examQueryRequest 考试查询 dto
     * @param teacher          学生信息
     * @return 分页考试列表
     */
    Page<ExamVO> getStudentAllExam(ExamQueryRequest examQueryRequest, User teacher);

    /**
     * 查询某个学生考试列表(考试中且学生为考试)
     *
     * @param examQueryRequest 考试查询 dto
     * @param student          学生信息
     * @return 分页考试列表
     */
    Page<ExamVO> getStudentExamCenter(ExamQueryRequest examQueryRequest, User student);
}
