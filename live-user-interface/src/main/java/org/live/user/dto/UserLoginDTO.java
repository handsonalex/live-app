package org.live.user.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserLoginDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -1786164736625284469L;

    private Boolean isLoginSuccess;

    private Long userId;

    private String desc;

    private String token;

    public static UserLoginDTO loginError(String desc){
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setIsLoginSuccess(false);
        userLoginDTO.setDesc(desc);
        return userLoginDTO;
    }

    public static UserLoginDTO loginSuccess(Long userId,String token){
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setIsLoginSuccess(true);
        userLoginDTO.setUserId(userId);
        userLoginDTO.setToken(token);
        return userLoginDTO;
    }

}
