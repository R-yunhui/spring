package com.ral.young.study.service;

import org.springframework.stereotype.Service;

/**
 *
 * @author renyunhui
 * @date 2023-12-11 15:27
 * @since 1.0.0
 */
@Service
public class MemberService {

    public double getHighOrder(Double price) {
        return price * 0.8;
    }

    public double getMidOrder(Double price) {
        return price * 0.9;
    }

    public double getNormalOrder(Double price) {
        return price * 0.95;
    }
}
