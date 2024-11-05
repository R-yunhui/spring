package com.ral.young.basic.snapshot;

import lombok.Data;

import java.io.Serializable;

/**
 * @author renyunhui
 * @description 这是一个SnapshotVO类
 * @date 2024-10-29 16-54-23
 * @since 1.0.0
 */
@Data
public class SnapshotVO implements Serializable {

    private String rtspUrl;
}
