package com.example.webswipeservice.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.webswipeservice.mapper.user.UserInfoMapper;
import com.example.webswipeservice.modal.user.IUserDetails;
import com.example.webswipeservice.modal.user.UserInfo;
import com.example.webswipeservice.modal.user.controller.RegisterUserInfo;
import com.example.webswipeservice.network.ResultUtils;
import com.example.webswipeservice.service.user.UserInfoService;
import com.example.webswipeservice.tools.JwtUtil;
import com.example.webswipeservice.tools.QlyTool;
import com.example.webswipeservice.tools.RedisCache;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.model.DefaultPutRet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Value("${qly-user.access-key}")
    String accessKey;
    @Value("${qly-user.secret-key}")
    String secretKey;

    @Value("${qly-buckets.web-swipe-user-avatar.domain}")
    String avatarDomain;
    @Value("${qly-buckets.web-swipe-user-avatar.bucket}")
    String avatarBucket;

    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;  // 用来对用户密码加密

    @Resource
    private RedisCache redisCache;

    @Override
    @Transactional
    public HashMap<String, String> login(UserInfo userInfo){
        // 1. 获取登录信息
        String loginUsername = userInfo.getUsername();
        String loginPassword = userInfo.getPassword();

        // 2. 获取数据库中用户信息，spring security的要求需要把UserInfo用IUserDetails封装一层
        IUserDetails userDetails = (IUserDetails) userDetailsService.loadUserByUsername(loginUsername);

        // 3. 对比登录信息和数据库中的信息，如果登录成功，生成token
        if (userDetails.getUserInfo() != null && passwordEncoder.matches(loginPassword, userDetails.getUserInfo().getPassword())) {
            // 根据用户信息生成token
            String jwt = JwtUtil.createJWT(String.valueOf(userDetails.getUserInfo().getId()));
            HashMap<String, String> jwtTokenMap = new HashMap<>();
            jwtTokenMap.put("webSwipeToken",jwt);
            // 存到redis, 用来查询token是否过期. 如果过期, 则说明用户需要重新登录. 用户成功登录则将token存入redis
            redisCache.setCacheObject("userId:"+userDetails.getUserInfo().getId(),
                    userDetails.getUserInfo(),3, TimeUnit.HOURS);
            return jwtTokenMap;
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public boolean register(RegisterUserInfo registerUserInfo) throws QiniuException {
        String username = registerUserInfo.getUsername();
        LambdaQueryWrapper<UserInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserInfo::getUsername, username);
        UserInfo dbUserInfo = userInfoMapper.selectOne(lambdaQueryWrapper);
        if (dbUserInfo == null) { // 判断用户名是否存在
            // 1. 上传头像
            Response response = QlyTool.uploadSrc2Qly(registerUserInfo.getAvatar(), avatarBucket, accessKey, secretKey);
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            String avatarKey = putRet.key;

            // 2. 保存用户到数据库
            UserInfo userInfo = new UserInfo();
            userInfo.setUsername(registerUserInfo.getUsername());
            userInfo.setPassword(passwordEncoder.encode(registerUserInfo.getPassword()));
            userInfo.setAvatarKey(avatarKey);
            userInfoMapper.insert(userInfo);
            return true;
        } else {
            return false;
        }
    }
}
