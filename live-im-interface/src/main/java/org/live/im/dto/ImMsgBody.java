package org.live.im.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ImMsgBody implements Serializable {
    @Serial
    private static final long serialVersionUID = -6603181945490551143L;

    /**
     * 接入im服务的各个业务线id
     */
    private int appId;

    private long userId;
    /**
     * 从业务服务中获取，用于在im服务建立连接时候使用
     */
    private String token;
    /**
     * 和业务服务进行消息传递
     */
    private String data;
}
