package com.example.webswipeservice.service.userinteraction;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.webswipeservice.modal.user.UserInfo;
import com.example.webswipeservice.modal.user.controller.RegisterUserInfo;
import com.example.webswipeservice.modal.userinteraction.UserInteraction;
import com.qiniu.common.QiniuException;


public interface UserInteractionService extends IService<UserInteraction> {
    void add(UserInteraction userInteraction);
    void delete(UserInteraction userInteraction);
}
