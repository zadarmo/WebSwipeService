package com.example.webswipeservice.filter;

import com.example.webswipeservice.modal.user.UserInfo;
import com.example.webswipeservice.tools.JwtUtil;
import com.example.webswipeservice.tools.RedisCache;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * JWT 过滤器，继承OncePerRequestFilter
 * OncePerRequestFilter只会对外部请求过滤一次，后续容器内的forward不会触发
 * 对用户的请求进行分发:
 *      需要身份验证的接口：验证token 并 存储当前用户信息到SecurityContextHolder
 *      不需要验证：直接doFilter
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("webSwipeToken");
        if(!StringUtils.hasText(token)){
            //无token：未携带或不需要token验证
                // 未携带会在后续报错，不需要token则直接放行
            //放行给其他过滤器
            filterChain.doFilter(request,response);
            return;
        }
        String userId;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("token 解析错误");
        }
        UserInfo currentUser = redisCache.getCacheObject("userId:" + userId);
        if (Objects.isNull(currentUser)) {
            throw new RuntimeException("用户登录过期,请重新登录");
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(currentUser, null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request,response);
    }
}
