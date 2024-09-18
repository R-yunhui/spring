SET NAMES utf8mb4;

-- 用户表
DROP TABLE IF EXISTS `ral_user`;
CREATE TABLE `ral_user`
(
    `id`            BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `ral_user_name` VARCHAR(33) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
    `ral_gender`    VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '性别',
    `ral_age`       INT(0)                                                          DEFAULT NULL COMMENT '算法id',
    `create_time`   datetime(0) NULL DEFAULT CURRENT_TIMESTAMP (0) COMMENT '创建时间',
    `update_time`   datetime(0) NULL DEFAULT CURRENT_TIMESTAMP (0) ON UPDATE CURRENT_TIMESTAMP (0) COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- 订单表
DROP TABLE IF EXISTS `ral_order`;
CREATE TABLE `ral_order`
(
    `id`                 BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `ral_order_name`     VARCHAR(33) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '订单名称',
    `ral_order_price`    DOUBLE  DEFAULT NULL COMMENT '订单价格',
    `ral_order_remark`   VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '订单备注',
    `ral_belong_user_id` BIGINT(20) NOT NULL COMMENT '所属用户id',
    `create_time`        datetime(0) NULL DEFAULT CURRENT_TIMESTAMP (0) COMMENT '创建时间',
    `update_time`        datetime(0) NULL DEFAULT CURRENT_TIMESTAMP (0) ON UPDATE CURRENT_TIMESTAMP (0) COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';
