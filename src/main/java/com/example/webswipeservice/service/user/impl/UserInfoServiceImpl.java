package com.example.webswipeservice.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.webswipeservice.mapper.user.UserInfoMapper;
import com.example.webswipeservice.modal.user.UserInfo;
import com.example.webswipeservice.modal.user.controller.RegisterUserInfo;
import com.example.webswipeservice.service.user.UserInfoService;
import com.example.webswipeservice.tools.QlyTool;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.model.DefaultPutRet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;

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
    PasswordEncoder passwordEncoder;  // 用来对用户密码加密

    @Override
    @Transactional
    public void register(RegisterUserInfo registerUserInfo) throws QiniuException {
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
    }
}
