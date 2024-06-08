package com.zj.z95exam.service;

import com.zj.z95exam.model.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zj.z95exam.model.entity.User;

import java.util.List;

/**
 * @author zj
 * @description 针对表【course(课程表)】的数据库操作Service
 * @createDate 2024-06-02 07:24:25
 */
public interface CourseService extends IService<Course> {
    /**
     * 返回老师所在班级的课程列表
     *
     * @param teacher 老师信息
     * @return 课程列表
     */
    List<Course> listCourse(User teacher);

    /**
     * 添加课程
     *
     * @param courseName 课程名称
     * @return 是否添加成功
     */
    boolean addCourse(String courseName);
}