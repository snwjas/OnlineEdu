-- MySQL dump 10.13  Distrib 8.0.18, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: online_edu
-- ------------------------------------------------------
-- Server version	8.0.24

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `acl_permission`
--

DROP TABLE IF EXISTS `acl_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `acl_permission` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '菜单权限表',
  `pid` int unsigned NOT NULL COMMENT '父级id',
  `type` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '菜单类型，0顶部菜单、1聚合菜单、2页面菜单、3接口',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '路由名称或接口名称',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单路径(以http开头时，视为打开外部链接) 或 请求接口的地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '菜单组件，只有叶子菜单才可配置',
  `meta` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '菜单的route.meta配置项，json格式',
  `admin` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '0默认，1只有超级管理员才能使用',
  `enable` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '是否启用，0否1是',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `acl_permission`
--

LOCK TABLES `acl_permission` WRITE;
/*!40000 ALTER TABLE `acl_permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `acl_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `acl_role`
--

DROP TABLE IF EXISTS `acl_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `acl_role` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `name` varchar(31) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '角色名称',
  `permission_id` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '角色具有的权限ID串',
  `enable` tinyint(1) DEFAULT '1' COMMENT '是否启用，0否1是',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='角色';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `acl_role`
--

LOCK TABLES `acl_role` WRITE;
/*!40000 ALTER TABLE `acl_role` DISABLE KEYS */;
INSERT INTO `acl_role` VALUES (1,'默认管理员','',1,'2021-01-18 19:30:48','2021-01-18 19:30:48'),(2,'普通管理员','',1,'2021-05-02 22:03:57','2021-05-02 22:03:57'),(3,'课程管理员','',1,'2021-05-02 22:04:08','2021-05-02 22:04:00');
/*!40000 ALTER TABLE `acl_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `acl_user`
--

DROP TABLE IF EXISTS `acl_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `acl_user` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '密码',
  `nickname` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '昵称',
  `avatar` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '用户头像',
  `mark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '备注',
  `sign` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '用户签名',
  `roleId` int unsigned NOT NULL COMMENT '角色id',
  `enable` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '是否启用，0否1是',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='管理员用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `acl_user`
--

LOCK TABLES `acl_user` WRITE;
/*!40000 ALTER TABLE `acl_user` DISABLE KEYS */;
INSERT INTO `acl_user` VALUES (1,'admin','ea48576f30be1669971699c09ad05c94','默认管理员','https://c-ssl.duitang.com/uploads/item/201912/05/20191205152830_ULrYx.thumb.300_0.jpeg','默认管理员，默认启用','无心插柳柳成荫，没什么期待才会偶遇惊喜！',1,1,'2022-02-20 18:26:37','2021-01-18 19:40:03'),(2,'tch1','ea48576f30be1669971699c09ad05c94','管理员1','https://c-ssl.duitang.com/uploads/item/201912/05/20191205152830_ULrYx.thumb.300_0.jpeg','管理员1号','无心插柳柳成荫，没什么期待才会偶遇惊喜！',2,1,'2022-02-20 18:26:37','2021-05-01 23:53:25'),(3,'tch2','ea48576f30be1669971699c09ad05c94','管理员2','https://c-ssl.duitang.com/uploads/item/201912/05/20191205152830_ULrYx.thumb.300_0.jpeg','管理员2号号号号','无心插柳柳成荫，没什么期待才会偶遇惊喜！',2,0,'2022-02-20 18:26:37','2021-05-01 23:55:22'),(4,'tch3','ea48576f30be1669971699c09ad05c94','课程管理员3','https://c-ssl.duitang.com/uploads/item/201912/05/20191205152830_ULrYx.thumb.300_0.jpeg','课程管理员3号','无心插柳柳成荫，没什么期待才会偶遇惊喜！',3,1,'2022-02-20 18:26:37','2021-05-01 23:55:22'),(5,'tch4','ea48576f30be1669971699c09ad05c94','管理员4','https://c-ssl.duitang.com/uploads/item/201912/05/20191205152830_ULrYx.thumb.300_0.jpeg','管理员4号','无心插柳柳成荫，没什么期待才会偶遇惊喜！',2,1,'2022-02-20 18:26:37','2021-05-01 23:55:22'),(6,'tch5','ea48576f30be1669971699c09ad05c94','管理员5','https://c-ssl.duitang.com/uploads/item/201912/05/20191205152830_ULrYx.thumb.300_0.jpeg','管理员5号','无心插柳柳成荫，没什么期待才会偶遇惊喜！',2,0,'2022-02-20 18:26:38','2021-05-01 23:55:22'),(7,'tch6','ea48576f30be1669971699c09ad05c94','管理员6','https://c-ssl.duitang.com/uploads/item/201912/05/20191205152830_ULrYx.thumb.300_0.jpeg','管理员6号','无心插柳柳成荫，没什么期待才会偶遇惊喜！',2,1,'2022-02-20 18:26:38','2021-05-01 23:55:22'),(8,'tch7','ea48576f30be1669971699c09ad05c94','管理员7','https://c-ssl.duitang.com/uploads/item/201912/05/20191205152830_ULrYx.thumb.300_0.jpeg','管理员7号','无心插柳柳成荫，没什么期待才会偶遇惊喜！',2,0,'2022-02-20 18:26:38','2021-05-01 23:55:22'),(9,'tch8','ea48576f30be1669971699c09ad05c94','管理员8','https://c-ssl.duitang.com/uploads/item/201912/05/20191205152830_ULrYx.thumb.300_0.jpeg','管理员8号','无心插柳柳成荫，没什么期待才会偶遇惊喜！',2,1,'2022-02-20 18:26:38','2021-05-01 23:55:22'),(10,'tch9','ea48576f30be1669971699c09ad05c94','管理员9','https://c-ssl.duitang.com/uploads/item/201912/05/20191205152830_ULrYx.thumb.300_0.jpeg','管理员9号','无心插柳柳成荫，没什么期待才会偶遇惊喜！',2,1,'2022-02-20 18:26:38','2021-05-01 23:55:22'),(11,'tch10','ea48576f30be1669971699c09ad05c94','管理员10','https://c-ssl.duitang.com/uploads/item/201912/05/20191205152830_ULrYx.thumb.300_0.jpeg','管理员10号','无心插柳柳成荫，没什么期待才会偶遇惊喜！',2,1,'2022-02-20 18:26:38','2021-05-01 23:55:22'),(12,'tch11','ea48576f30be1669971699c09ad05c94','管理员11','https://c-ssl.duitang.com/uploads/item/201912/05/20191205152830_ULrYx.thumb.300_0.jpeg','管理员11号','无心插柳柳成荫，没什么期待才会偶遇惊喜！',2,1,'2022-02-20 18:26:38','2021-05-01 23:55:22'),(13,'tch12','ea48576f30be1669971699c09ad05c94','管理员12','https://c-ssl.duitang.com/uploads/item/201912/05/20191205152830_ULrYx.thumb.300_0.jpeg','管理员12号','无心插柳柳成荫，没什么期待才会偶遇惊喜！',2,0,'2022-02-20 18:26:39','2021-05-01 23:55:22'),(14,'tch13','ea48576f30be1669971699c09ad05c94','管理员13','https://c-ssl.duitang.com/uploads/item/201912/05/20191205152830_ULrYx.thumb.300_0.jpeg','管理员13号','无心插柳柳成荫，没什么期待才会偶遇惊喜！',2,1,'2022-02-20 18:26:39','2021-05-01 23:55:22'),(15,'tch14','ea48576f30be1669971699c09ad05c94','管理员14','https://c-ssl.duitang.com/uploads/item/201912/05/20191205152830_ULrYx.thumb.300_0.jpeg','管理员14号','无心插柳柳成荫，没什么期待才会偶遇惊喜！',2,1,'2022-02-20 18:26:39','2021-05-01 23:55:22'),(16,'tch15','ea48576f30be1669971699c09ad05c94','管理员15','https://c-ssl.duitang.com/uploads/item/201912/05/20191205152830_ULrYx.thumb.300_0.jpeg','管理员15号','无心插柳柳成荫，没什么期待才会偶遇惊喜！',2,0,'2022-02-20 18:26:39','2021-05-01 23:55:22'),(17,'tch16','ea48576f30be1669971699c09ad05c94','管理员16','https://c-ssl.duitang.com/uploads/item/201912/05/20191205152830_ULrYx.thumb.300_0.jpeg','管理员16号','无心插柳柳成荫，没什么期待才会偶遇惊喜！',2,1,'2022-02-20 18:26:39','2021-05-01 23:55:22');
/*!40000 ALTER TABLE `acl_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `edu_chapter`
--

DROP TABLE IF EXISTS `edu_chapter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_chapter` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `course_id` int unsigned NOT NULL COMMENT '课程ID',
  `title` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '章节名称',
  `sort` int DEFAULT '0' COMMENT '显示排序',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=COMPACT COMMENT='课程章节表';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `edu_chapter_tmp`
--

DROP TABLE IF EXISTS `edu_chapter_tmp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_chapter_tmp` (
  `id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '主键',
  `oid` int unsigned DEFAULT '0' COMMENT '原章节ID',
  `course_id` int unsigned NOT NULL COMMENT '课程ID',
  `title` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '章节名称',
  `sort` int DEFAULT '0' COMMENT '显示排序',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_id` (`oid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=COMPACT COMMENT='课程章节临时表（用于存放二次修改的数据）';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `edu_comment`
--

DROP TABLE IF EXISTS `edu_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_comment` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `course_id` int unsigned NOT NULL COMMENT '课程id',
  `teacher_id` int unsigned NOT NULL COMMENT '讲师id',
  `member_id` int unsigned NOT NULL COMMENT '会员id',
  `content` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '评论内容',
  `mark` double unsigned DEFAULT '5' COMMENT '评分（满分5.00）',
  `status` tinyint unsigned DEFAULT NULL COMMENT '评论状态 0审核中 1通过',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_course_id` (`course_id`) USING BTREE,
  KEY `idx_teacher_id` (`teacher_id`) USING BTREE,
  KEY `idx_member_id` (`member_id`) USING BTREE,
  KEY `idx_member_id_course_id` (`member_id`,`course_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=651 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='课程评论表';
/*!40101 SET character_set_client = @saved_cs_client */;
--
-- Table structure for table `edu_course`
--

DROP TABLE IF EXISTS `edu_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_course` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `teacher_id` int unsigned NOT NULL COMMENT '课程讲师ID',
  `subject_id` int unsigned NOT NULL COMMENT '课程科目分类ID',
  `title` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '课程标题',
  `price` double(10,2) unsigned DEFAULT '0.00' COMMENT '课程销售价格，设置为0则可免费观看',
  `lesson_num` int unsigned NOT NULL DEFAULT '0' COMMENT '总课时',
  `cover` varchar(1023) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '课程封面图片路径',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '课程描述',
  `buy_count` int unsigned DEFAULT '0' COMMENT '销售数量',
  `view_count` int unsigned DEFAULT '0' COMMENT '浏览数量',
  `sort` int DEFAULT '0' COMMENT '显示排序',
  `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '上架下架，0下架 1上架',
  `status` tinyint DEFAULT '0' COMMENT '课程状态，草稿 审核 发表',
  `remarks` varchar(511) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=COMPACT COMMENT='课程表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_subject`
--

DROP TABLE IF EXISTS `edu_subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_subject` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '课程类别ID',
  `title` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类别名称',
  `parent_id` int unsigned DEFAULT '0' COMMENT '父ID',
  `sort` int unsigned DEFAULT '0' COMMENT '排序字段',
  `enable` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '是否启用，0否1是',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_parent_id` (`parent_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=COMPACT COMMENT='课程科目分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_teacher`
--

DROP TABLE IF EXISTS `edu_teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_teacher` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '讲师ID',
  `mobile` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '手机号',
  `email` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '邮箱地址',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '密码',
  `name` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '讲师姓名',
  `intro` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '讲师简介',
  `avatar` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '讲师头像',
  `resume` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '讲师简历链接',
  `division` tinyint DEFAULT '80' COMMENT '分成比例，0-100',
  `sort` int DEFAULT '0' COMMENT '排序',
  `enable` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '是否启用，0否1是',
  `status` tinyint DEFAULT '0' COMMENT '讲师状态：审核通过；审核不通过；待审核',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_name` (`name`) USING BTREE,
  UNIQUE KEY `uk_mobile` (`mobile`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='讲师表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_video`
--

DROP TABLE IF EXISTS `edu_video`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_video` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '视频ID',
  `course_id` int unsigned NOT NULL COMMENT '课程ID',
  `chapter_id` int unsigned NOT NULL COMMENT '章节ID',
  `title` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '视频显示名称',
  `video_id` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '云端视频资源',
  `sort` int DEFAULT '0' COMMENT '排序字段',
  `play_count` int unsigned DEFAULT '0' COMMENT '播放次数',
  `free` tinyint unsigned DEFAULT '1' COMMENT '是否可以试听：0免费 1收费',
  `duration` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '视频时长（秒）',
  `size` bigint unsigned NOT NULL DEFAULT '0' COMMENT '视频源文件大小（字节）',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_course_id` (`course_id`) USING BTREE,
  KEY `idx_chapter_id` (`chapter_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=257 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=COMPACT COMMENT='课程视频';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `edu_video_tmp`
--

DROP TABLE IF EXISTS `edu_video_tmp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_video_tmp` (
  `id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '主键(视频ID)',
  `oid` int unsigned DEFAULT '0' COMMENT '原视频ID',
  `course_id` int unsigned NOT NULL COMMENT '课程ID',
  `chapter_id` bigint unsigned NOT NULL COMMENT '章节ID',
  `title` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '视频显示名称',
  `video_id` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '云端视频资源',
  `sort` int DEFAULT '0' COMMENT '排序字段',
  `play_count` int unsigned DEFAULT '0' COMMENT '播放次数',
  `free` tinyint unsigned DEFAULT '1' COMMENT '是否可以试听：0免费 1收费',
  `duration` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '视频时长（秒）',
  `size` bigint unsigned NOT NULL DEFAULT '0' COMMENT '视频源文件大小（字节）',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_course_id` (`course_id`) USING BTREE,
  KEY `idx_chapter_id` (`chapter_id`) USING BTREE,
  KEY `idx_id` (`oid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=COMPACT COMMENT='课程视频表（用于存放二次修改的数据）';
/*!40101 SET character_set_client = @saved_cs_client */;
--
-- Table structure for table `hm_banner`
--

DROP TABLE IF EXISTS `hm_banner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hm_banner` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '标题',
  `image_url` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '图片地址',
  `link_url` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '链接地址',
  `sort` int unsigned NOT NULL DEFAULT '0' COMMENT '排序',
  `enable` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '是否启用，0否1是',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='首页banner表';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `rel_course_member`
--

DROP TABLE IF EXISTS `rel_course_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rel_course_member` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `course_id` int unsigned NOT NULL DEFAULT '0' COMMENT '课程Id',
  `member_id` int unsigned NOT NULL DEFAULT '0' COMMENT '学员Id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_course_id` (`course_id`) USING BTREE,
  UNIQUE KEY `idx_member_id` (`member_id`) USING BTREE,
  KEY `idx_member_course` (`member_id`,`course_id`) USING BTREE COMMENT '联合索引(member_id, course_id)'
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='课程订阅-学员关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stat_daily`
--

DROP TABLE IF EXISTS `stat_daily`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stat_daily` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `date` datetime NOT NULL COMMENT '统计日期',
  `visit_count` int unsigned DEFAULT '0' COMMENT '访客数量',
  `register_count` int unsigned DEFAULT '0' COMMENT '注册人数',
  `login_count` int unsigned DEFAULT '0' COMMENT '活跃人数',
  `video_view_count` int unsigned DEFAULT '0' COMMENT '视频播放数',
  `course_buy_count` int unsigned DEFAULT '0' COMMENT '购买数量',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `statistics_day` (`date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1826 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='网站统计日数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_message`
--

DROP TABLE IF EXISTS `sys_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_message` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '消息id',
  `from_id` int unsigned NOT NULL DEFAULT '0' COMMENT '发送者Id',
  `from_role` tinyint NOT NULL DEFAULT '0' COMMENT '发送者角色(管理员、讲师...)',
  `to_id` int unsigned NOT NULL DEFAULT '0' COMMENT '接受者id',
  `to_role` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '接受者角色(教师、学员...)',
  `title` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '消息标题',
  `content` varchar(511) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '消息内容',
  `has_read` tinyint unsigned DEFAULT '0' COMMENT '是否已读(0未读 1已读)',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_to_id` (`to_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='消息表';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `t_order`
--

DROP TABLE IF EXISTS `t_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_order` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `order_no` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '订单号(datetime+unsigned int)',
  `course_id` int unsigned NOT NULL COMMENT '课程id',
  `member_id` int unsigned NOT NULL COMMENT '会员id',
  `total_fee` double(10,2) DEFAULT '0.01' COMMENT '订单金额（分）',
  `pay_type` tinyint unsigned DEFAULT '0' COMMENT '支付类型（0 未支付 1：微信 2：支付宝）',
  `transaction_num` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '交易流水号',
  `pay_time` datetime DEFAULT NULL COMMENT '支付完成时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `ux_order_no` (`order_no`) USING BTREE,
  KEY `idx_course_id` (`course_id`) USING BTREE,
  KEY `idx_member_id` (`member_id`) USING BTREE,
  KEY `idx_member_id_course_id` (`member_id`,`course_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `uctr_member`
--

DROP TABLE IF EXISTS `uctr_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `uctr_member` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '会员id',
  `mobile` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '手机号',
  `email` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '邮箱地址',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '密码',
  `nickname` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '昵称',
  `sex` tinyint unsigned DEFAULT '0' COMMENT '性别 1 女，2 男',
  `age` tinyint unsigned DEFAULT '0' COMMENT '年龄',
  `avatar` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '用户头像',
  `sign` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '用户签名',
  `enable` tinyint(1) DEFAULT '1' COMMENT '是否启用，0否1是',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_mobile` (`mobile`) USING BTREE,
  UNIQUE KEY `uk_nickname` (`nickname`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1203 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='会员表';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Dumping routines for database 'online_edu'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

