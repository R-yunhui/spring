package com.ral.young.spring.basic.service;

import javax.validation.groups.Default;

/**
 * @author renyunhui
 * @description 这是一个Update类
 * @date 2024-11-21 15-44-04
 * @since 1.0.0
 */
public interface CustomValidateGroup extends Default {

    interface Crud extends CustomValidateGroup {

        interface Create extends Crud {

        }

        interface Update extends Crud {

        }

        interface Query extends Crud {

        }

        interface Delete extends Crud {

        }
    }
}
