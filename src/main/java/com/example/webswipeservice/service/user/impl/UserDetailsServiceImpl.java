package com.example.webswipeservice.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.webswipeservice.mapper.user.UserInfoMapper;
import com.example.webswipeservice.modal.user.IUserDetails;
import com.example.webswipeservice.modal.user.UserInfo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 该类实现了Spring Security 过滤器链种涉及的UserDetailService
 * 用于获取当前用户信息
 * @author Ys
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        //查询用户信息
        LambdaQueryWrapper<UserInfo> userInfoQueryWrapper = new LambdaQueryWrapper<>();
        userInfoQueryWrapper.eq(UserInfo::getUsername,userName);
        UserInfo currentUser = userInfoMapper.selectOne(userInfoQueryWrapper);
        if(Objects.isNull(currentUser)){
            throw  new RuntimeException("用户不存在");
        }
        //如果引入了RBAC再考虑权限
        //...
        //封装为UserDetails返回，最终会被存储到SecurityContextHolder

        return new IUserDetails(currentUser);
    }
}
