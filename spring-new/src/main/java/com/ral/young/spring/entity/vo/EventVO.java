package com.ral.young.spring.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author renyunhui
 * @description 这是一个EventVO类
 * @date 2025-02-05 14-39-51
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventVO {

    private Long eventId;

    private String time;

    private String eventName;
}
