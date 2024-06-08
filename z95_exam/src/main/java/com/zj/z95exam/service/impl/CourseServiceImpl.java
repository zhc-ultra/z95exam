package com.zj.z95exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.z95exam.model.entity.Course;
import com.zj.z95exam.model.entity.User;
import com.zj.z95exam.service.CourseService;
import com.zj.z95exam.mapper.CourseMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zj
 * @description 针对表【course(课程表)】的数据库操作Service实现
 * @createDate 2024-06-02 07:24:25
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course>
        implements CourseService {

    /**
     * 获取课程列表
     *
     * @param teacher 老师信息
     * @return 课程列表
     */
    @Override
    public List<Course> listCourse(User teacher) {
        // 课程创建者
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        // 使用MyBatis-Plus 提供的list 方法查询课程列表
        return list(queryWrapper);
    }

    /**
     * 添加课程
     *
     * @param courseName 课程名称
     * @return 是否添加成功
     */
    @Override
    public boolean addCourse(String courseName) {
        // 使用QueryWrapper 凭借查询条件，查询要添加的课程是否存在，不存在才进行添加
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", courseName);
        // 如果课程不存在就创建，存在就拦截
        Course course = getOne(queryWrapper);
        if (course != null) {
            return false;
        }
        course = new Course();
        course.setName(courseName);

        // save() 方法 将 course 对象插入到数据库中
        // TODO 注意！！！这些操作数据库的方法(list(),save(),getById(),getOne()...)都是通过继承MyBatis-Plus实现的ServiceImpl<?,?> 父类而获得的
        return save(course);
    }
}




