/*
 Navicat Premium Data Transfer

 Source Server         : zlj_localhost
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : unit_s

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 17/01/2023 09:18:14
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` varchar(64) NOT NULL,
  `sex` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `age` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户';

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES ('1', '男', '张三', 18);
INSERT INTO `user` VALUES ('2', '男', '李四', 21);
INSERT INTO `user` VALUES ('3', '女', '王丽', 22);
COMMIT;

-- ----------------------------
-- Table structure for user_man
-- ----------------------------
DROP TABLE IF EXISTS `user_man`;
CREATE TABLE `user_man` (
  `id` varchar(64) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `sex_m` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of user_man
-- ----------------------------
BEGIN;
INSERT INTO `user_man` VALUES ('1', '张三', '男');
COMMIT;

-- ----------------------------
-- Table structure for user_woman
-- ----------------------------
DROP TABLE IF EXISTS `user_woman`;
CREATE TABLE `user_woman` (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `sex_w` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of user_woman
-- ----------------------------
BEGIN;
INSERT INTO `user_woman` VALUES ('2', '小林', '女');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
