CREATE TABLE `user`
(
    `id`          BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`        VARCHAR(50)  DEFAULT NULL COMMENT '用户名',
    `age`         INT          DEFAULT NULL COMMENT '年龄',
    `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `gender`      VARCHAR(20)  DEFAULT NULL COMMENT '性别',
    `is_delete`   TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标识(0-未删除，1-已删除)',
    `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT NULL COMMENT '更新时间',
    `create_user` BIGINT       DEFAULT NULL COMMENT '创建人ID',
    `update_user` BIGINT       DEFAULT NULL COMMENT '更新人ID',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';