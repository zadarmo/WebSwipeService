package com.example.webswipeservice.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.webswipeservice.modal.user.UserInfo;
import com.example.webswipeservice.modal.user.controller.RegisterUserInfo;
import com.qiniu.common.QiniuException;
import org.springframework.stereotype.Service;


public interface UserInfoService extends IService<UserInfo> {
    void register(RegisterUserInfo registerUserInfo) throws QiniuException;
}
