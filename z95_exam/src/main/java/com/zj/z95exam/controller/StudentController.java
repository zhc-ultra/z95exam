package com.zj.z95exam.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zj.z95exam.annotation.AuthCheck;
import com.zj.z95exam.common.BaseResponse;
import com.zj.z95exam.common.ErrorCode;
import com.zj.z95exam.common.ResultUtils;
import com.zj.z95exam.exception.BusinessException;
import com.zj.z95exam.model.dto.exam.ExamQueryRequest;
import com.zj.z95exam.model.dto.examsubmit.ExamSubmitAddRequest;
import com.zj.z95exam.model.dto.examsubmit.ExamSubmitQueryRequest;
import com.zj.z95exam.model.entity.*;
import com.zj.z95exam.model.vo.ExamSubmitVO;
import com.zj.z95exam.model.vo.ExamVO;
import com.zj.z95exam.service.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 学生端接口控制器
 * 处理学生相关的请求
 * @date 2024/6/3 18:57
 **/
@RestController("/student")
public class StudentController {
    /*********************************************注入业务层Bean            start            注入业务层Bean**************************************************/
    // 业务层依赖注入
    @Resource
    UserService userService;

    @Resource
    ExamService examService;

    @Resource
    ExamSubmitService examSubmitService;
    /*********************************************注入业务层Bean------------end------------注入业务层Bean**************************************************/

    /*********************************************学生端接口实现------------start------------学生端接口实现**************************************************/


    /**************************************************************考试相关接口*********************************************************************/

    /**
     * 学生端 - 获取学生所有考试
     *
     * @param examQueryRequest dto对象 - 考试查询请求 - 封装考试查询请求的多个参数
     * @param request          http请求 携带请求的附加信息
     * @return 返回学生所有考试的分页列表
     */
    @PostMapping("/exam_list")
    @AuthCheck(mustRole = "student")
    public BaseResponse<Page<ExamVO>> getStudentAllExam(@RequestBody ExamQueryRequest examQueryRequest, HttpServletRequest request) {
        if (examQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取登录学生信息
        User student = userService.getLoginUser(request);
        // 获取学生所有考试分页信息
        Page<ExamVO> examVOPage = examService.getStudentAllExam(examQueryRequest, student);
        return ResultUtils.success(examVOPage);
    }

    /**
     * 学生端 - 获取考试中心列表
     *
     * @param examQueryRequest dto对象 - 考试查询请求 - 封装考试查询请求的多个参数
     * @param request          http请求 携带请求的附加信息
     * @return 返回考试中心的分页列表
     */
    @PostMapping("/exam_center_list")
    @AuthCheck(mustRole = "student")
    public BaseResponse<Page<ExamVO>> getStudentExamCenter(@RequestBody ExamQueryRequest examQueryRequest, HttpServletRequest request) {
        if (examQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取登录学生信息
        User student = userService.getLoginUser(request);
        // 获取考试中心分页信息
        Page<ExamVO> examVOPage = examService.getStudentExamCenter(examQueryRequest, student);
        return ResultUtils.success(examVOPage);
    }

    /************************************************************考试提交相关接口*********************************************************************/

    /**
     * 学生端 - 提交考试
     *
     * @param examSubmitAddRequest dto对象 - 考试提交请求 - 封装考试提交的多个请求参数
     * @param request              http请求 携带请求的附加信息
     * @return 返回提交考试是否成功
     */
    @PostMapping("/exam_submit")
    @AuthCheck(mustRole = "student")
    public BaseResponse<Boolean> submitExam(@RequestBody ExamSubmitAddRequest examSubmitAddRequest, HttpServletRequest request) {
        if (examSubmitAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取登录学生信息
        User student = userService.getLoginUser(request);
        // 获取考试信息
        Exam exam = examService.getById(examSubmitAddRequest.getExamId());
        // 提交考试
        boolean result = examSubmitService.submitExam(examSubmitAddRequest, exam, student);
        return ResultUtils.success(result);
    }

    /**
     * 学生端 - 根据学生id和考试id查询考试提交
     *
     * @param examSubmitQueryRequest dto对象 - 考试提交查询请求 - 封装考试提交查询的多个参数
     * @param request                http请求 携带请求的附加信息
     * @return 返回考试提交的VO对象
     */
    @PostMapping("/query_exam_submit")
    public BaseResponse<ExamSubmitVO> queryExamSubmitById(@RequestBody ExamSubmitQueryRequest examSubmitQueryRequest, HttpServletRequest request) {
        // 获取考试id
        Long examId = examSubmitQueryRequest.getExamId();
        // 获取考试信息
        Exam exam = examService.getById(examId);
        // 获取登录学生信息
        User user = userService.getLoginUser(request);
        Long userId = user.getId();

        // 查询条件封装
        QueryWrapper<ExamSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("userId", userId)
                .eq("examId", examId);
        // 获取考试提交信息
        ExamSubmit examSubmit = examSubmitService.getOne(queryWrapper);
        examSubmitQueryRequest.setId(examSubmit.getId());
        examSubmitQueryRequest.setExamId(examSubmit.getExamId());
        examSubmitQueryRequest.setUserId(examSubmit.getUserId());
        // 获取考试提交的VO对象
        ExamSubmitVO examSubmitVO = examSubmitService.queryExamSubmitById(examSubmitQueryRequest, exam, examSubmit, user);
        return ResultUtils.success(examSubmitVO);
    }
    /*********************************************教师端接口实现------------end------------教师端接口实现**************************************************/
}
