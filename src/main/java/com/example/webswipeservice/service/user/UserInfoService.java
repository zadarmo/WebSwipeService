package com.example.webswipeservice.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.webswipeservice.modal.user.UserInfo;
import com.example.webswipeservice.modal.user.controller.RegisterUserInfo;
import com.qiniu.common.QiniuException;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;


public interface UserInfoService extends IService<UserInfo> {

    HashMap<String, String> login(UserInfo userInfo);
    void register(RegisterUserInfo registerUserInfo) throws QiniuException;
}
