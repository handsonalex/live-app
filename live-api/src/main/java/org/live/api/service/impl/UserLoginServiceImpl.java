package org.live.api.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.live.account.interfaces.IAccountTokenRpc;
import org.live.api.service.IUserLoginService;
import org.live.api.vo.UserLoginVO;
import org.live.common.interfaces.utils.ConvertBeanUtils;
import org.live.common.interfaces.vo.WebResponseVO;
import org.live.msg.dto.MsgCheckDTO;
import org.live.msg.enums.MsgSendResultEnum;
import org.live.msg.interfaces.ISmsRpc;
import org.live.user.dto.UserLoginDTO;
import org.live.user.interfaces.IUserPhoneRpc;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * @Author idea
 * @Date: Created in 10:51 2023/6/15
 * @Description
 */
@Service
@Slf4j
public class UserLoginServiceImpl implements IUserLoginService {

    private static String PHONE_REG = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";

    @DubboReference
    private ISmsRpc smsRpc;
    @DubboReference
    private IUserPhoneRpc userPhoneRPC;

    @DubboReference
    private IAccountTokenRpc accountTokenRpc;

    @Override
    public WebResponseVO sendLoginCode(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return WebResponseVO.errorParam("手机号不能为空");
        }
        if (!Pattern.matches(PHONE_REG, phone)) {
            return WebResponseVO.errorParam("手机号格式异常");
        }
        MsgSendResultEnum msgSendResultEnum = smsRpc.sendLoginCode(phone);
        if (msgSendResultEnum == MsgSendResultEnum.SEND_SUCCESS) {
            return WebResponseVO.success();
        }
        return WebResponseVO.sysError("短信发送太频繁，请稍后再试");
    }

    @Override
    public WebResponseVO login(String phone, Integer code, HttpServletResponse response) {
        if (StringUtils.isEmpty(phone)) {
            return WebResponseVO.errorParam("手机号不能为空");
        }
        if (!Pattern.matches(PHONE_REG, phone)) {
            return WebResponseVO.errorParam("手机号格式异常");
        }
        if (code == null || code < 1000) {
            return WebResponseVO.errorParam("验证码格式异常");
        }
        MsgCheckDTO msgCheckDTO = smsRpc.checkLoginCode(phone, code);
        if (!msgCheckDTO.isCheckStatus()) {
            return WebResponseVO.bizError(msgCheckDTO.getDesc());
        }
        //验证码校验通过
        UserLoginDTO userLoginDTO = userPhoneRPC.login(phone);
        if (!userLoginDTO.isLoginSuccess()){
            log.info("login has error,phone is {}",phone);
            return WebResponseVO.sysError();
        }
        String token = accountTokenRpc.createAndSaveLoginToken(userLoginDTO.getUserId());
        Cookie cookie = new Cookie("qytk",token);
        //http://app.berber.live.com/html/live_list_room.html
        //http://api.berber.live.com/live/api/userLogin/sendLoginCode
        cookie.setDomain("live.com");
        cookie.setPath("/");
        //cookie有效期，一般他的默认单位是秒
        cookie.setMaxAge(30 * 24 * 3600);
        response.addCookie(cookie);
        return WebResponseVO.success(ConvertBeanUtils.convert(userLoginDTO, UserLoginVO.class));
    }
}
