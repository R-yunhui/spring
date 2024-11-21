CREATE TABLE `user`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`        varchar(30) NOT NULL COMMENT '姓名',
    `age`         int                  DEFAULT NULL COMMENT '年龄',
    `email`       varchar(50)          DEFAULT NULL COMMENT '邮箱',
    `gender`      tinyint     NOT NULL DEFAULT '0' COMMENT '性别：0-未知，1-男，2-女',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    `create_user` bigint      NOT NULL COMMENT '创建人',
    `update_user` bigint      NOT NULL COMMENT '更新人',
    `is_delete`   tinyint     NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 添加version字段 支持乐观锁
ALTER TABLE `user`
    ADD COLUMN `version` int NOT NULL DEFAULT 1 COMMENT '乐观锁版本号' AFTER `gender`;