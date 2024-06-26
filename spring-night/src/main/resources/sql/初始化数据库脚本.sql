CREATE TABLE `night_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '人员ID',
  `name` varchar(255) NOT NULL COMMENT '姓名',
  `sex` tinyint(1) NOT NULL COMMENT '性别，1男2女',
  `age` tinyint(4) NOT NULL COMMENT '年龄',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除 1是，0否',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `create_user` bigint(20) NOT NULL COMMENT '创建人ID',
  `update_user` bigint(20) NOT NULL COMMENT '修改人ID',
  PRIMARY KEY (`id`),
  KEY `idx_name_age` (`name`,`age`)
) ENGINE=InnoDB AUTO_INCREMENT=1762665173376181671 DEFAULT CHARSET=utf8mb4;