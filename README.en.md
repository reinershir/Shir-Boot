# SHIR-BOOT

<div align="center">
  <p>
  </p>

[**English**](README.en.md) | **简体中文** |

</div>

Shir-Boot快速开发平台，整合了后台管理系统常用功能、国际化、在线代码生成等

### 基于

* Spring boot 3.0
* Mybatis plus
* [lui-auth 2.x](https://github.com/reinershir/lui-auth)
* Mysql 5.8
* Redis
* Swagger 3
* i18n
* [vue-element-admin](https://github.com/PanJiaChen/vue-element-admin)
* Jdk 17 +

### 使用

1. 下载源码
2. 新建自己的包名
3. 修改`ShirBootApplication.java` 中 `@SpringBootApplication(scanBasePackages = "io.github.reinershir.boot","你的包路径")`以及 `@MapperScan(value = {"io.github.reinershir.boot.mapper","你的mapper路径"})`
4. 初始化数据库
5. 下载[前端源码](https://github.com/reinershir/Shir-Boot-Admin)并运行
6. 运行`ShirBootApplication.java` 启动后台服务

### 数据库初始化SQL

后续将会把SQL文件放在项目中

```sql

-- 导出 shir-boot 的数据库结构
CREATE DATABASE IF NOT EXISTS `shir-boot` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `shir-boot`;

-- 导出  表 shir-boot.MENU 结构
CREATE TABLE IF NOT EXISTS `MENU` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `NAME` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '菜单名称',
  `URL` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '跳转地址',
  `ICON` varchar(300) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '图标',
  `PERMISSION_CODES` varchar(150) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '标识权限码',
  `DESCRIPTION` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `LEFT_VALUE` int NOT NULL COMMENT '左节点值',
  `RIGHT_VALUE` int NOT NULL COMMENT '右节点值',
  `LEVEL` int NOT NULL COMMENT '节点等级',
  `PROPERTY` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '属性(自由使用标识)',
  `CREATE_DATE` datetime NOT NULL,
  `UPDATE_DATE` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- 正在导出表  shir-boot.MENU 的数据：~14 rows (大约)
INSERT INTO `MENU` (`ID`, `NAME`, `URL`, `ICON`, `PERMISSION_CODES`, `DESCRIPTION`, `LEFT_VALUE`, `RIGHT_VALUE`, `LEVEL`, `PROPERTY`, `CREATE_DATE`, `UPDATE_DATE`) VALUES
	(1, 'System management', '', 'el-icon-setting', NULL, '', 1, 28, 1, 'menu', '2023-11-01 16:15:56', '2023-11-01 16:47:06'),
	(2, 'User management', '/system/UserList', 'user', 'USER:LIST', NULL, 2, 11, 2, 'menu', '2023-11-01 16:25:57', '2023-11-01 16:43:54'),
	(3, 'Add user', NULL, NULL, 'USER:ADD', NULL, 3, 4, 3, 'button', '2023-11-01 16:28:18', NULL),
	(4, 'Update user', NULL, NULL, 'USER:UPDATE', NULL, 5, 6, 3, 'button', '2023-11-01 16:30:03', NULL),
	(5, 'Delete user', NULL, NULL, 'USER:DELETE', NULL, 7, 8, 3, 'button', '2023-11-01 16:30:25', NULL),
	(6, 'Reset password', NULL, NULL, 'USER:RESETPASSWORD', NULL, 9, 10, 3, 'button', '2023-11-01 16:31:25', NULL),
	(7, 'Role management', '/system/RoleList', 'people', 'ROLE:LIST', '', 12, 19, 2, 'menu', '2023-11-01 16:32:09', '2023-11-01 16:44:00'),
	(8, 'Add role', NULL, NULL, 'ROLE:ADD', NULL, 13, 14, 3, 'button', '2023-11-01 16:35:52', NULL),
	(9, 'Update role', NULL, NULL, 'ROLE:UPDATE', NULL, 15, 16, 3, 'button', '2023-11-01 16:36:12', NULL),
	(10, 'Delete role', NULL, NULL, 'ROLE:DELETE', NULL, 17, 18, 3, 'button', '2023-11-01 16:36:28', NULL),
	(11, 'Menu management', '/system/MenuList', 'component', NULL, NULL, 20, 27, 2, 'menu', '2023-11-01 16:39:02', '2023-11-01 16:44:08'),
	(12, 'Add menu', NULL, NULL, 'MENU:ADD', NULL, 21, 22, 3, 'button', '2023-11-01 16:39:17', NULL),
	(13, 'Update menu', NULL, NULL, 'MENU:UPDATE', NULL, 23, 24, 3, 'button', '2023-11-01 16:39:36', NULL),
	(14, 'Delete menu', NULL, NULL, 'MENU:DELETE', NULL, 25, 26, 3, 'button', '2023-11-01 16:39:56', NULL);

-- 导出  表 shir-boot.ROLE 结构
CREATE TABLE IF NOT EXISTS `ROLE` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `ROLE_NAME` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '角色名称',
  `DESCRIPTION` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `CREATE_DATE` datetime NOT NULL,
  `UPDATE_DATE` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- 正在导出表  shir-boot.ROLE 的数据：~2 rows (大约)
INSERT INTO `ROLE` (`ID`, `ROLE_NAME`, `DESCRIPTION`, `CREATE_DATE`, `UPDATE_DATE`) VALUES
	(5, 'admin', 'admin', '2023-11-02 14:50:06', '2023-11-03 11:16:57'),
	(6, 'manager', 'manager', '2023-11-02 15:29:37', '2023-11-03 11:16:02');

-- 导出  表 shir-boot.ROLE_PERMISSION 结构
CREATE TABLE IF NOT EXISTS `ROLE_PERMISSION` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `ROLE_ID` bigint NOT NULL,
  `MENU_ID` bigint NOT NULL,
  `PERMISSION_CODES` varchar(150) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `ROLE_ID_INDEX` (`ROLE_ID`) USING BTREE,
  KEY `MENU_ID_INDEX` (`MENU_ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=159 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- 正在导出表  shir-boot.ROLE_PERMISSION 的数据：~20 rows (大约)
INSERT INTO `ROLE_PERMISSION` (`ID`, `ROLE_ID`, `MENU_ID`, `PERMISSION_CODES`) VALUES
	(98, 6, 1, NULL),
	(99, 6, 2, 'USER:LIST'),
	(100, 6, 3, 'USER:ADD'),
	(101, 6, 4, 'USER:UPDATE'),
	(102, 6, 5, 'USER:DELETE'),
	(103, 6, 6, 'USER:RESETPASSWORD'),
	(145, 5, 1, NULL),
	(146, 5, 2, 'USER:LIST'),
	(147, 5, 3, 'USER:ADD'),
	(148, 5, 4, 'USER:UPDATE'),
	(149, 5, 5, 'USER:DELETE'),
	(150, 5, 6, 'USER:RESETPASSWORD'),
	(151, 5, 7, 'ROLE:LIST'),
	(152, 5, 8, 'ROLE:ADD'),
	(153, 5, 9, 'ROLE:UPDATE'),
	(154, 5, 10, 'ROLE:DELETE'),
	(155, 5, 11, NULL),
	(156, 5, 12, 'MENU:ADD'),
	(157, 5, 13, 'MENU:UPDATE'),
	(158, 5, 14, 'MENU:DELETE');

-- 导出  表 shir-boot.ROLE_USER 结构
CREATE TABLE IF NOT EXISTS `ROLE_USER` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `ROLE_ID` bigint NOT NULL,
  `USER_ID` varchar(150) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- 正在导出表  shir-boot.ROLE_USER 的数据：~3 rows (大约)
INSERT INTO `ROLE_USER` (`ID`, `ROLE_ID`, `USER_ID`) VALUES
	(6, 6, '2'),
	(7, 5, '2'),
	(8, 5, '3'),
	(9, 5, '5'),
	(10, 6, '6');

-- 导出  表 shir-boot.USER 结构
CREATE TABLE IF NOT EXISTS `USER` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `PASSWORD` varchar(64) NOT NULL,
  `LOGIN_NAME` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登陆名',
  `NICK_NAME` varchar(100) NOT NULL,
  `EMAIL` varchar(50) DEFAULT NULL,
  `PHONE_NUMBER` varchar(50) DEFAULT NULL,
  `PROFILE` varchar(300) DEFAULT NULL COMMENT '用户头像',
  `CREATE_DATE` datetime NOT NULL,
  `UPDATE_DATE` datetime DEFAULT NULL,
  `STATUS` int NOT NULL DEFAULT '0' COMMENT '状态，0=正常，1=禁用',
  `REMARK` varchar(100) DEFAULT NULL,
  `IS_DELETE` int NOT NULL DEFAULT '0' COMMENT '删除状态，0=正常，1=删除',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- 正在导出表  shir-boot.USER 的数据：~4 rows (大约)
INSERT INTO `USER` (`ID`, `PASSWORD`, `LOGIN_NAME`, `NICK_NAME`, `EMAIL`, `PHONE_NUMBER`, `PROFILE`, `CREATE_DATE`, `UPDATE_DATE`, `STATUS`, `REMARK`, `IS_DELETE`) VALUES
	(1, '7a57a5a743894a0e4a801fc3e19e9301f1abe6a46b8d3256cda0829a', 'admin', 'admin', '123', '123123', 'https://reiner.host/img/head.jpg', '2023-07-31 17:28:36', '2023-10-10 18:12:25', 0, NULL, 0),
	(5, 'bfa312155cb9449cc92cd8e8e19e9301f1abe6a46b8d3256cda0829a', 'monkeyKing', 'monkeyKing', '', '', 'https://tse4-mm.cn.bing.net/th/id/OIP-C.2vUTawLyzalDoTv7zF6JTQHaEo?pid=ImgDet&rs=1', '2023-11-03 15:47:49', NULL, 0, '', 0),
	(6, 'a4cb5697348e081f5753a33ce19e9301f1abe6a46b8d3256cda0829a', 'miyuki', 'miyuki', '', '', 'https://pic3.zhimg.com/50/v2-07ed9a142c3a8337f15c24e7a09b3d34_720w.jpg?source=1940ef5c', '2023-11-03 15:51:06', '2023-11-03 15:51:39', 0, '', 0);

```

