package com.zj.z95exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zj.z95exam.annotation.AuthCheck;
import com.zj.z95exam.common.BaseResponse;
import com.zj.z95exam.common.ErrorCode;
import com.zj.z95exam.common.ResultUtils;
import com.zj.z95exam.exception.BusinessException;
import com.zj.z95exam.model.dto.course.CourseAddRequest;
import com.zj.z95exam.model.dto.exam.ExamAddRequest;
import com.zj.z95exam.model.dto.exam.ExamQueryRequest;
import com.zj.z95exam.model.dto.exam.ExamUpdateRequest;
import com.zj.z95exam.model.dto.examsubmit.ExamSubmitQueryRequest;
import com.zj.z95exam.model.dto.question.QuestionAddRequest;
import com.zj.z95exam.model.dto.question.QuestionDeleteRequest;
import com.zj.z95exam.model.dto.question.QuestionQueryRequest;
import com.zj.z95exam.model.dto.question.QuestionUpdateRequest;
import com.zj.z95exam.model.dto.user.*;
import com.zj.z95exam.model.entity.*;
import com.zj.z95exam.model.vo.ExamSubmitVO;
import com.zj.z95exam.model.vo.ExamVO;
import com.zj.z95exam.model.vo.QuestionVO;
import com.zj.z95exam.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 教师端的相关接口控制器
 */
// @RestController = ResponseBody(返回数据) + Controller
// @Controller 直接返回页面，随着前后端的分离
// 基本上也就不会直接使用@Controller返回页面了
@RestController
// @RequestMapping 路由映射
// 后端收到请求时，会根据前端请求的路径，和Mapping进行模式匹配
// 从而找到正确的控制器，处理这个请求
// 此处写在类上方，表示类路径，即下方修饰方法的Mapping路径都需要带上
// '/teacher' 前缀进行访问
@RequestMapping("/teacher")
// 门面模式
// 日志框架，会根据你选择的日志实现自动处理日志
@Slf4j
public class TeacherController {
    /*********************************************注入业务层Bean            start            注入业务层Bean**************************************************/
    // @Resource 依赖注入，顾名思义，将Spring容器托管的
    // Bean(简单理解为Spring 创建的对象) 注入到 userService 内部
    // 相当于 主动 new 了一个对象，但是使用依赖注入可以降低对象的创建和使用之间的耦合
    @Resource
    // 用户相关操作的业务层
    private UserService userService;
    @Resource
    // 考试相关操作的业务层
    private ExamService examService;
    @Resource
    // 题目相关操作的业务层
    private QuestionService questionService;
    @Resource
    // 考试提交相关操作的业务层
    private ExamSubmitService examSubmitService;
    @Resource
    // 课程相关操作的业务层
    private CourseService courseService;

    /*********************************************注入业务层Bean------------end------------注入业务层Bean**************************************************/


    /*********************************************教师端接口实现------------start------------教师端接口实现**************************************************/

    /**************************************学生相关接口------------start------------学生相关接口*********************************************/

    /**
     * 教师端-学生管理-获取学生列表
     *
     * @param userQueryRequest dto对象 - 学生查询请求 - 封装学生查询请求的多个参数
     * @param request          http请求 携带这http 请求的附加信息，如cookie(浏览器存储数据的机制)
     * @return 返回学生分页列表
     */
    // @AuthCheck
    // 权限校验注解，使用AOP切面实现，对接口调用者的身份进行鉴权，仅教师能访问该接口
    @AuthCheck(mustRole = "teacher")
    // @PostMapping
    // 此处注解修饰方法，'/student_list' 应该加在类路径 '/teacher' 路径之后
    // 完整的访问路径为: '/teacher/student_list' 但是项目配置了基础路径 content-path '/api'
    // 所以接口最后的访问路径应该为: '/api/teacher/student_list'
    @PostMapping("/student_list")
    // 接口的实现
    // BaseResponse 全局响应处理，同一响应的格式，将数据封装到响应的 data 内
    // @RequestBody
    // 自动将Json格式的数据转换成对象(不需要再手动处理)
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest, HttpServletRequest request) {
        // 参数校验，请求查询数据不能为空
        if (userQueryRequest == null) {
            // 为空直接抛出异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 使用 cookie 从session 、 数据库 中获取用户，在session中查到数据后
        // 会在数据库内重写查询，（session是本地缓存，可能产生数据不一致，追求极致效率，可以直接走缓存）
        User loginUser = userService.getLoginUser(request);
        // 调用业务层，获取分页结构
        Page<User> userPage = userService.listUser(userQueryRequest, loginUser);
        // 使用全局响应处理工具类，包装响应数据
        return ResultUtils.success(userPage);
    }
    /**************************************学生相关接口------------end------------学生相关接口*********************************************/

    /**************************************考试相关接口------------start------------考试相关接口*********************************************/
    /**
     * 给教师所在班级添加一场考试
     * 教师端-查看考试信息-添加考试
     * 教师端-创建新考试
     *
     * @param examAddRequest dto对象 - 考试添加请求 - 封装考试添加的多个请求参数，如考试内容、考试课程等待
     * @param request        http请求 携带请求的附加信息
     * @return 返回添加成功的的考试的id 添加失败放回-1
     */
    // @AuthCheck 权限校验注解，该接口只允许教师访问
    @AuthCheck(mustRole = "teacher")
    // 路由映射 - 方法路径
    @PostMapping("/exam_add")
    public BaseResponse<Long> addExam(@RequestBody ExamAddRequest examAddRequest, HttpServletRequest request) {
        // 参数校验
        if (examAddRequest == null) {
            // 请求参数为空，直接使用全局异常出路，抛出异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 调用业务层，处理业务
        User teacher = userService.getLoginUser(request);
        long examId = examService.addExam(examAddRequest, teacher);
        return ResultUtils.success(examId);
    }

    /**
     * 教师端 - 查看考试信息 - 删除考试
     *
     * @param examQueryRequest dto 封装考试删除的相关参数
     * @param request          携带请求的附加信息
     * @return 返回删除考试是否成功
     */
    // 接口仅教师能够访问
    @AuthCheck(mustRole = "teacher")
    // 路由映射
    @PostMapping("/exam_delete")
    public BaseResponse<Boolean> deleteExam(@RequestBody ExamQueryRequest examQueryRequest, HttpServletRequest request) {
        // 参数校验
        if (examQueryRequest == null || examQueryRequest.getId() == null) {
            // 抛出异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 调用业务层,处理业务
        boolean result = examService.deleteExam(examQueryRequest.getId());
        // 返回业务层的处理结果 true or false
        return ResultUtils.success(result);
    }

    /**
     * 教师端 - 查看考试信息 - 修改考试
     *
     * @param examUpdateRequest dto对象封装更新考试的参数
     * @param request           http请求携带的其他参数
     * @return 返回更新考试是否成功
     */
    // 限制接口的访问用户
    @AuthCheck(mustRole = "teacher")
    // 路由映射
    @PostMapping("/exam_update")
    public BaseResponse<Boolean> updateExam(@RequestBody ExamUpdateRequest examUpdateRequest, HttpServletRequest request) {
        // 参数校验
        if (examUpdateRequest == null) {
            // 抛出异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 调用业务层处理业务
        boolean res = examService.updateExam(examUpdateRequest);
        // 返回业务层的处理结果
        return ResultUtils.success(res);
    }

    /**
     * 教师端 - 查看考试信息 - 考卷详情
     *
     * @param id      考卷id
     * @param request http 请求附带信息,如cookie...
     * @return 返回考试的VO对象(对考试数据进行脱敏和补充)
     */
    @PostMapping("/exam_query")
    public BaseResponse<ExamVO> queryExamById(Long id, HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        Exam exam = examService.getById(id);
        Course course = courseService.getById(exam.getCourseId());
        ExamVO examVO = examService.queryExamById(id, course, user);
        return ResultUtils.success(examVO);
    }

    /**
     * 教师端 - 查看考试信息 - 获取班级考试列表
     * 查看自己班级的所有考试
     *
     * @param examQueryRequest dto对象 - 考试查询请求 - 封装考试查询请求的多个参数
     * @param request          http请求 携带这http 请求的附加信息，如cookie(浏览器存储数据的机制)
     * @return 返回分页的班级考试列表
     */
    // 权限校验注解，使用AOP切面实现，对接口调用者的身份进行鉴权，仅教师能访问该接口
    @AuthCheck(mustRole = "teacher")
    // @PostMapping
    // 此处注解修饰方法，'/exam_list' 应该加在类路径 '/teacher' 路径之后
    // 完整的访问路径为: '/teacher/exam_list' 但是项目配置了基础路径 content-path '/api'
    // 所以接口最后的访问路径应该为: '/api/teacher/exam_list'
    @PostMapping("/exam_list")
    // 接口实现
    // @RequestBody 注解 自动解析请求的Json数据为Java-dto对象
    public BaseResponse<Page<Exam>> listExamByPage(@RequestBody ExamQueryRequest examQueryRequest, HttpServletRequest request) {
        // 参数校验 - 考试分页查询请求不能为空
        if (examQueryRequest == null) {
            // 为空直接抛出异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 调用 userService 业务层获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 调用 examService 业务层 获取班级考试列表
        Page<Exam> examPage = examService.listExam(examQueryRequest, loginUser);
        // 使用全局响应工具类封装响应
        return ResultUtils.success(examPage);
    }

    /**************************************考试相关接口------------end------------考试相关接口*********************************************/

    /**************************************考试提交相关接口------------start------------考试提交相关接口*********************************************/
    /**
     * 教师端 - 成绩查询 - 获取学生考试提交列表
     *
     * @param examSubmitQueryRequest dto - 学生考试提交查询请求封装
     * @param request                http请求
     * @return 返回学生考试成绩列表
     */
    // 限制访问接口的用户角色
    @AuthCheck(mustRole = "teacher")
    // 路由映射
    @PostMapping("/student_score_list")
    public BaseResponse<Page<ExamSubmit>> queryExamSubmitByPage(@RequestBody ExamSubmitQueryRequest examSubmitQueryRequest, HttpServletRequest request) {
        // 参数校验
        if (examSubmitQueryRequest == null) {
            // 抛出异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 调用业务层处理业务
        User teacher = userService.getLoginUser(request);
        Page<ExamSubmit> examSubmitPage = examSubmitService.queryExamSubmitByPage(examSubmitQueryRequest, teacher);
        // 使用全局响应工具列包装并返回业务层处理的结果
        return ResultUtils.success(examSubmitPage);
    }

    /**
     * 教师端 - 成绩查询 - 查看学生作答情况
     * 查看某个学生的考试情况
     *
     * @param examSubmitQueryRequest dto - 封装考试提交查询请求
     * @param request                http 请求
     * @return 返回考试提交的VO对象
     */
    @AuthCheck(mustRole = "teacher")
    @PostMapping("/query_exam_submit")
    public BaseResponse<ExamSubmitVO> queryExamSubmitById(@RequestBody ExamSubmitQueryRequest examSubmitQueryRequest, HttpServletRequest request) {
        // 参数校验
        if (examSubmitQueryRequest == null) {
            // 抛出异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 补充 Service 层需要的参数，如非必要，尽可能不要在Service层调用其他Service层
        // 这样容易产生Bean 之间的循环依赖

        // 联动其他业务层，补充业务层所需的参数
        Long examSubmitId = examSubmitQueryRequest.getId();
        // 考试提交信息
        ExamSubmit examSubmit = examSubmitService.getById(examSubmitId);
        Long id = examSubmit.getUserId();
        // 交卷的学生信息
        User student = userService.getById(id);
        Long examId = examSubmit.getExamId();
        // 试卷信息
        Exam exam = examService.getById(examId);

        Long courseId = exam.getCourseId();
        Course course = courseService.getById(courseId);
        // 调用业务层 某个学生获取考试提交
        ExamSubmitVO examSubmitVO = examSubmitService.queryExamSubmitById(examSubmitId, student, exam, course);
        return ResultUtils.success(examSubmitVO);
    }

    /**************************************考试提交相关接口------------end------------考试提交相关接口*********************************************/

    /**************************************题目相关接口------------start------------题目相关接口*********************************************/
    /**
     * 教师端 - 题目管理 - 添加题目
     *
     * @param questionAddRequest dto对象 - 题目添加请求 - 封装题目添加的多个请求参数
     * @param request            http请求 携带请求的附加信息
     * @return 返回添加题目是否成功
     */
    @AuthCheck(mustRole = "teacher")
    @PostMapping("/question_add")
    public BaseResponse<Boolean> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        // 参数校验
        if (questionAddRequest == null) {
            // 请求参数为空
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if (questionAddRequest.getContent() == null || questionAddRequest.getAnswer() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User teacher = userService.getLoginUser(request);
        // 调用业务层获取结果
        boolean result = questionService.addQuestion(questionAddRequest, teacher);
        return ResultUtils.success(result);
    }

    /**
     * 教师端 - 题目管理 - 删除题目
     *
     * @param questionDeleteRequest dto对象 - 题目删除请求 - 封装题目删除的多个请求参数
     * @param request               http请求 携带请求的附加信息
     * @return 返回删除题目是否成功
     */
    @AuthCheck(mustRole = "teacher")
    @PostMapping("/question_delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody QuestionDeleteRequest questionDeleteRequest, HttpServletRequest request) {
        if (questionDeleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = questionDeleteRequest.getId();
        boolean result = questionService.deleteQuestion(id);
        return ResultUtils.success(result);
    }

    /**
     * 教师端 - 题目管理 - 更新题目
     *
     * @param questionUpdateRequest dto对象 - 题目更新请求 - 封装题目更新的多个请求参数
     * @param request               http请求 携带请求的附加信息
     * @return 返回更新题目是否成功
     */
    @AuthCheck(mustRole = "teacher")
    @PostMapping("/question_update")
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest, HttpServletRequest request) {
        if (questionUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = questionService.updateQuestion(questionUpdateRequest);

        return ResultUtils.success(result);
    }

    /**
     * 教师端 - 题目管理 - 修改题目
     *
     * @param questionQueryRequest dto对象 - 题目查询请求 - 封装题目查询的多个请求参数
     * @param request              http请求 携带请求的附加信息
     * @return 返回查询到的题目信息
     */
    @AuthCheck(mustRole = "teacher")
    @PostMapping("/question_query")
    public BaseResponse<QuestionVO> queryQuestionById(QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionVO questionVO = questionService.queryQuestionById(questionQueryRequest);
        return ResultUtils.success(questionVO);
    }

    /**
     * 教师端 - 题目管理 - 获取题目分页列表
     *
     * @param questionQueryRequest dto对象 - 题目查询请求 - 封装题目查询的多个请求参数
     * @param request              http请求 携带请求的附加信息
     * @return 返回题目的分页列表
     */
    @AuthCheck(mustRole = "teacher")
    @PostMapping("/question_query_page")
    public BaseResponse<Page<QuestionVO>> queryQuestionPage(@RequestBody QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
        // 创建者的 id
        User teacher = userService.getLoginUser(request);
        if (teacher == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<QuestionVO> questionVOPage = questionService.queryQuestionVOPage(questionQueryRequest, teacher, request);
        return ResultUtils.success(questionVOPage);
    }
    /**************************************题目相关接口--------------end------------题目相关接口*********************************************/

    /**************************************课程相关接口------------start------------课程相关接口*********************************************/
    /**
     * 教师端 - 创建考试 - 获取课程列表供用户选择
     *
     * @param request http请求 携带请求的附加信息
     * @return 返回教师的课程列表
     */
    @AuthCheck(mustRole = "teacher")
    @GetMapping("/course_list")
    public BaseResponse<List<Course>> getCourseList(HttpServletRequest request) {
        User teacher = userService.getLoginUser(request);
        if (teacher == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Course> courseList = courseService.listCourse(teacher);
        return ResultUtils.success(courseList);
    }

    /**
     * 教师端 - 查看考试信息 - 添加课程
     *
     * @param courseAddRequest dto对象 - 课程添加请求 - 封装课程添加的多个请求参数
     * @param request          http请求 携带请求的附加信息
     * @return 返回添加课程是否成功
     */
    @AuthCheck(mustRole = "teacher")
    @PostMapping("/course_add")
    public BaseResponse<Boolean> addCourse(@RequestBody CourseAddRequest courseAddRequest, HttpServletRequest request) {
        if (courseAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String courseName = courseAddRequest.getCourseName();
        boolean result = courseService.addCourse(courseName);
        return ResultUtils.success(result);
    }
    /**************************************课程相关接口------------end------------课程相关接口*********************************************/

    /*********************************************教师端接口实现------------end------------教师端接口实现**************************************************/

}