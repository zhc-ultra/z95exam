-- 创建库
CREATE DATABASE IF NOT EXISTS z95;

-- 切换库
USE z95;

-- 班级表
CREATE TABLE IF NOT EXISTS class
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    className  VARCHAR(256)                       NOT NULL COMMENT '班级名',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete   TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除'
) COMMENT '班级';

-- 用户表
CREATE TABLE IF NOT EXISTS user
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    classId      BIGINT                                 NOT NULL COMMENT '班级id',
    userAccount  VARCHAR(256)                           NOT NULL COMMENT '账号',
    userPassword VARCHAR(512)                           NOT NULL COMMENT '密码',
    userName     VARCHAR(256) COMMENT '用户昵称',
    userAvatar   VARCHAR(1024) COMMENT '用户头像',
    userProfile  VARCHAR(512) COMMENT '用户简介',
    userRole     VARCHAR(256) DEFAULT 'user'            NOT NULL COMMENT '用户角色：user/teacher/admin/ban',
    createTime   DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime   DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete     TINYINT      DEFAULT 0                 NOT NULL COMMENT '是否删除',
    FOREIGN KEY (classId) REFERENCES class (id)
) COMMENT '用户';

-- 用户关系表
CREATE TABLE IF NOT EXISTS user_relation
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId       BIGINT                             NOT NULL COMMENT '用户 id',
    relationType VARCHAR(256) COMMENT '关系类型（师生）',
    createTime   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_userId (userId),
    FOREIGN KEY (userId) REFERENCES user (id)
) COMMENT '用户关系';

-- 题目表
CREATE TABLE IF NOT EXISTS question
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(512) COMMENT '标题',
    content    TEXT COMMENT '内容',
    type       VARCHAR(256) COMMENT '考试类型（单选题、多选题、判断题）',
    tags       VARCHAR(1024) COMMENT '标签列表（json 数组）',
    answer     TEXT COMMENT '题目答案',
    userId     BIGINT                             NOT NULL COMMENT '创建用户 id',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete   TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除',
    INDEX idx_userId (userId),
    FOREIGN KEY (userId) REFERENCES user (id)
) COMMENT '题目';

-- 考试表
CREATE TABLE IF NOT EXISTS exam
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(512) COMMENT '标题',
    content    TEXT COMMENT '内容',
    courseId   BIGINT                             NOT NULL COMMENT '课程 id',
    tags       VARCHAR(1024) COMMENT '标签列表（json 数组）',
    teacherId  BIGINT                             NOT NULL COMMENT '创建老师 id',
    examDate   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '考试日期',
    examTime   int COMMENT '考试时长',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete   TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除',
    INDEX idx_teacherId (teacherId),
    FOREIGN KEY (teacherId) REFERENCES user (id)
) COMMENT '考试';

-- 考试提交表（交完自动同步判卷）
CREATE TABLE IF NOT EXISTS exam_submit
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId     BIGINT                             NOT NULL COMMENT '考试学生 id',
    examId     BIGINT                             NOT NULL COMMENT '考试 id',
    score      INT COMMENT '分数',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete   TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除',
    INDEX idx_examId (examId),
    INDEX idx_userId (userId),
    FOREIGN KEY (examId) REFERENCES exam (id),
    FOREIGN KEY (userId) REFERENCES user (id)
) COMMENT '考试提交';

-- 课程表
CREATE TABLE IF NOT EXISTS course
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(256) NOT NULL COMMENT '课程名'
) COMMENT '课程表';
