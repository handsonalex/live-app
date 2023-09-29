package org.live.web.starter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.live.common.interfaces.enums.GatewayHeaderEnum;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 旗鱼直播 用户信息拦截器
 *
 * @Author idea
 * @Date: Created in 08:48 2023/6/25
 * @Description
 */
public class UserInfoInterceptor implements HandlerInterceptor {

    //所有web请求来到这里的时候，都要被拦截，controller之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userIdStr = request.getHeader(GatewayHeaderEnum.USER_LOGIN_ID.getName());
        //参数判断，userID是否为空
        //可能走的是白名单url
        if (StringUtils.isEmpty(userIdStr)) {
            return true;
        }
        //如果userId不为空，则把它放在线程本地变量里面去
        RequestContext.set(RequestConstants.USER_ID, Long.valueOf(userIdStr));
        return true;
    }

    //controller之后执行
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        RequestContext.clear();
    }
}
