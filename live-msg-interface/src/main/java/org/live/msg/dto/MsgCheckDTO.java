package org.live.msg.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class MsgCheckDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7239058020117736725L;
    private boolean checkStatus;
    private String desc;

    public MsgCheckDTO(boolean checkStatus, String desc) {
        this.checkStatus = checkStatus;
        this.desc = desc;
    }
    public boolean isCheckStatus() {
        return checkStatus;
    }



}
