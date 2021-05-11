package com.ral.admin.springcloud.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-13 10:14
 * @Describe:
 * @Modify:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookInfo {

    /** 主键ID */
    private int id;

    /** 书名 */
    private String bookName;

    /** 作者 */
    private String author;
}
