package org.live.user.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserCacheAsyncDeleteDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7752810054956497783L;
    /**
     * 不同业务场景的code，区别不同的延迟消息
     */
    private int code;

    private String json;
}


