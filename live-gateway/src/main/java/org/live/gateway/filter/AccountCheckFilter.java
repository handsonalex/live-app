package org.live.gateway.filter;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.live.account.interfaces.IAccountTokenRpc;
import org.live.common.interfaces.enums.GatewayHeaderEnum;
import org.live.gateway.properties.GatewayApplicationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static io.netty.handler.codec.http.cookie.CookieHeaderNames.MAX_AGE;
import static org.springframework.web.cors.CorsConfiguration.ALL;

/**
 * @author :Joseph Ho
 * Description: 服务请求过滤器，跨域配置、cookie传递
 * Date: 11:58 2023/9/22
 */
@Component
@Slf4j
public class AccountCheckFilter implements GlobalFilter, Ordered {

    @DubboReference
    private IAccountTokenRpc accountTokenRpc;

    @Resource
    private GatewayApplicationProperties gatewayApplicationProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求url，判断是否为空，如果为空则返回请求不通过
        ServerHttpRequest request = exchange.getRequest();
        String reqUrl = request.getURI().getPath();
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://web.live.com:5500");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, ALL);
        headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);
        if (reqUrl == null) {
            return Mono.empty();
        }
        //判断url是否在白名单内
        List<String> notCheckUrlList = gatewayApplicationProperties.getNotCheckUrlList();
        for (String notCheckUrl : notCheckUrlList) {
            if (reqUrl.startsWith(notCheckUrl)) {
                log.info("请求没有进行token校验，直接传达给业务下游");
                //直接请求转给下游
                return chain.filter(exchange);
            }
        }
        //不在白名单校验cookie
        List<HttpCookie> httpCookieList = request.getCookies().get("qytk");
        if (CollectionUtils.isEmpty(httpCookieList)){
            log.error("请求没有检索到qytk的cookie，被拦截");
            return Mono.empty();
        }
        String tokenCookieValue = httpCookieList.get(0).getValue();
        if (StringUtils.isEmpty(tokenCookieValue) || StringUtils.isBlank(tokenCookieValue)){
            log.error("请求的cookie中的qytk为空，被拦截");
            return Mono.empty();
        }
        //token获取之后，换取userId，传递到下游
        Long userId = accountTokenRpc.getUserIdByToken(tokenCookieValue);
        if (userId == null){
            log.error("请求的userId为空，被拦截");
            return Mono.empty();
        }
        //gateway ---(header)---> springboot-web(interceptor --> get header)
        ServerHttpRequest.Builder builder = request.mutate();
        builder.header(GatewayHeaderEnum.USER_LOGIN_ID.getName(), String.valueOf(userId));
        return chain.filter(exchange.mutate().request(builder.build()).build());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
