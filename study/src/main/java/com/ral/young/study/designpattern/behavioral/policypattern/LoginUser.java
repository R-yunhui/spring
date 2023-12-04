package com.ral.young.study.designpattern.behavioral.policypattern;

import lombok.Data;

/**
 *
 * @author renyunhui
 * @date 2023-12-04 10:05
 * @since 1.0.0
 */
@Data
public class LoginUser {

    private String username;

    private String password;

    private Integer permission;

    private Integer role;
}
