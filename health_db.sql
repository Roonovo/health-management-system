-- MySQL dump 10.13  Distrib 5.6.40, for Win64 (x86_64)
--
-- Host: localhost    Database: health_db
-- ------------------------------------------------------
-- Server version	5.6.40-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `real_name` varchar(50) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `status` tinyint(4) DEFAULT '1',
  `role` varchar(50) DEFAULT NULL,
  `last_login_time` datetime DEFAULT NULL,
  `last_login_ip` varchar(45) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `conversation`
--

DROP TABLE IF EXISTS `conversation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `conversation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '所属用户ID',
  `title` varchar(255) DEFAULT NULL COMMENT '对话标题',
  `last_message` text COMMENT '最后一条消息内容',
  `message_count` int(11) DEFAULT '0' COMMENT '消息总数内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话会话表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conversation`
--

LOCK TABLES `conversation` WRITE;
/*!40000 ALTER TABLE `conversation` DISABLE KEYS */;
/*!40000 ALTER TABLE `conversation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login_device`
--

DROP TABLE IF EXISTS `login_device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `login_device` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '所属用户ID',
  `device_name` varchar(100) DEFAULT NULL COMMENT '设备名称 (如: iPhone 15, Chrome Browser)',
  `device_type` varchar(20) DEFAULT NULL COMMENT '设备类型 (WEB, ANDROID, IOS)',
  `device_id` varchar(255) DEFAULT NULL COMMENT '设备唯一标识符',
  `ip_address` varchar(45) DEFAULT NULL COMMENT '登录IP地址 (兼容IPv6)',
  `location` varchar(255) DEFAULT NULL COMMENT '登录地理位置',
  `is_current` tinyint(1) DEFAULT '0' COMMENT '是否为当前登录设备: 1-是, 0-否',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录设备管理表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login_device`
--

LOCK TABLES `login_device` WRITE;
/*!40000 ALTER TABLE `login_device` DISABLE KEYS */;
/*!40000 ALTER TABLE `login_device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medical_document`
--

DROP TABLE IF EXISTS `medical_document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `medical_document` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '所属用户ID',
  `document_name` varchar(255) NOT NULL COMMENT '档案名称/文件名',
  `document_type` varchar(50) DEFAULT NULL COMMENT '档案类型 (如: pdf, jpg, png)',
  `file_path` varchar(512) DEFAULT NULL COMMENT '文件在服务器的物理路径',
  `file_url` varchar(512) DEFAULT NULL COMMENT '文件的访问URL',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小 (字节)',
  `upload_date` datetime DEFAULT NULL COMMENT '档案上传日期',
  `category` varchar(50) DEFAULT NULL COMMENT '分类 (如: 化验单, 影像报告, 病历)',
  `description` varchar(1024) DEFAULT NULL COMMENT '档案描述',
  `ocr_result` text COMMENT 'OCR识别结果文本',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`) COMMENT '用户查询索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医疗档案及OCR识别记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medical_document`
--

LOCK TABLES `medical_document` WRITE;
/*!40000 ALTER TABLE `medical_document` DISABLE KEYS */;
/*!40000 ALTER TABLE `medical_document` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medication_plan`
--

DROP TABLE IF EXISTS `medication_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `medication_plan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '所属用户ID',
  `medication_name` varchar(255) NOT NULL COMMENT '药品名称',
  `dosage` varchar(100) DEFAULT NULL COMMENT '用法用量 (如: 1片, 5ml)',
  `frequency` varchar(100) DEFAULT NULL COMMENT '用药频次 (如: 每日3次)',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `reminder_times` varchar(1000) DEFAULT '[]' COMMENT '提醒时间列表 (存储JSON格式: ["08:00","12:00"])',
  `notes` text COMMENT '备注信息',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态: 0-已停用, 1-进行中',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`) COMMENT '用户查询索引',
  KEY `idx_status` (`status`) COMMENT '状态筛选索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用药计划提醒表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medication_plan`
--

LOCK TABLES `medication_plan` WRITE;
/*!40000 ALTER TABLE `medication_plan` DISABLE KEYS */;
/*!40000 ALTER TABLE `medication_plan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medication_reminder`
--

DROP TABLE IF EXISTS `medication_reminder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `medication_reminder` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `plan_id` bigint(20) NOT NULL COMMENT '关联的计划ID (medication_plan.id)',
  `reminder_time` datetime NOT NULL COMMENT '预定的提醒时间',
  `status` varchar(20) DEFAULT 'PENDING' COMMENT '状态: PENDING-待提醒, SENT-已发送, CONFIRMED-已确认服药, MISSED-漏服',
  `actual_time` datetime DEFAULT NULL COMMENT '用户实际点击确认的时间',
  `note` varchar(500) DEFAULT NULL COMMENT '服药备注 (如: 饭后服用的反馈)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_plan_id` (`plan_id`) COMMENT '计划ID索引',
  KEY `idx_status_time` (`status`,`reminder_time`) COMMENT '状态与时间复合索引，方便定时任务扫描'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用药提醒执行记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medication_reminder`
--

LOCK TABLES `medication_reminder` WRITE;
/*!40000 ALTER TABLE `medication_reminder` DISABLE KEYS */;
/*!40000 ALTER TABLE `medication_reminder` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `conversation_id` bigint(20) NOT NULL COMMENT '关联的会话ID (conversation.id)',
  `content` text NOT NULL COMMENT '消息内容',
  `sender_type` varchar(10) NOT NULL COMMENT '发送者类型: USER-用户, AI-人工智能',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消息发送时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_conversation_id` (`conversation_id`) COMMENT '会话ID索引，用于快速加载聊天记录'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `password` varchar(255) NOT NULL COMMENT '加密后的密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别: male, female',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `height` double(5,2) DEFAULT NULL COMMENT '身高(cm)',
  `weight` double(5,2) DEFAULT NULL COMMENT '体重(kg)',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态: 0-禁用, 1-正常',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(45) DEFAULT NULL COMMENT '最后登录IP',
  `last_password_change` datetime DEFAULT NULL COMMENT '最后修改密码时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账号及健康基础资料表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_settings`
--

DROP TABLE IF EXISTS `user_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_settings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '所属用户ID',
  `medication_reminder` tinyint(1) DEFAULT '1' COMMENT '用药提醒开关: 1-开启, 0-关闭',
  `appointment_reminder` tinyint(1) DEFAULT '1' COMMENT '预约提醒开关: 1-开启, 0-关闭',
  `health_report_reminder` tinyint(1) DEFAULT '1' COMMENT '健康报告提醒开关',
  `system_notification` tinyint(1) DEFAULT '1' COMMENT '系统通知开关',
  `reminder_time` varchar(50) DEFAULT '08:00' COMMENT '全局提醒首选时间',
  `profile_visibility` tinyint(1) DEFAULT '0' COMMENT '个人资料可见性',
  `health_data_visibility` tinyint(1) DEFAULT '0' COMMENT '健康数据可见性',
  `allow_data_analysis` tinyint(1) DEFAULT '1' COMMENT '是否允许数据分析',
  `allow_third_party_sharing` tinyint(1) DEFAULT '0' COMMENT '是否允许第三方共享',
  `login_alert_enabled` tinyint(1) DEFAULT '1' COMMENT '登录异动提醒开关',
  `backup_email` varchar(100) DEFAULT NULL COMMENT '备用邮箱',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`) COMMENT '每个用户仅拥有一套设置'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户偏好与隐私设置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_settings`
--

LOCK TABLES `user_settings` WRITE;
/*!40000 ALTER TABLE `user_settings` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_settings` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-12 15:02:57
