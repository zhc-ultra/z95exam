package com.zj.z95exam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.z95exam.model.entity.Class;
import com.zj.z95exam.service.ClassService;
import com.zj.z95exam.mapper.ClassMapper;
import org.springframework.stereotype.Service;

/**
* @author zj
* @description 针对表【class(班级)】的数据库操作Service实现
* @createDate 2024-06-02 07:24:25
*/
@Service
public class ClassServiceImpl extends ServiceImpl<ClassMapper, Class>
    implements ClassService{

}