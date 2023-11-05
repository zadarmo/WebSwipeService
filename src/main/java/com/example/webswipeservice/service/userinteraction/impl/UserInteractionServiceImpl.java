package com.example.webswipeservice.service.userinteraction.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.webswipeservice.constant.EnumInteractionType;
import com.example.webswipeservice.mapper.user.UserInfoMapper;
import com.example.webswipeservice.mapper.userinteraction.UserInteractionMapper;
import com.example.webswipeservice.modal.user.UserInfo;
import com.example.webswipeservice.modal.user.controller.RegisterUserInfo;
import com.example.webswipeservice.modal.userinteraction.UserInteraction;
import com.example.webswipeservice.service.user.UserInfoService;
import com.example.webswipeservice.service.userinteraction.UserInteractionService;
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

import java.util.Date;
import java.util.Objects;

@Service
public class UserInteractionServiceImpl extends ServiceImpl<UserInteractionMapper, UserInteraction> implements UserInteractionService {

    @Autowired
    UserInteractionMapper userInteractionMapper;

    @Override
    public void add(UserInteraction userInteraction) {
        Date date = new Date();
        userInteraction.setOperateAt(date);
        userInteractionMapper.insert(userInteraction);
    }

    @Override
    public void delete(UserInteraction userInteraction) {
        // userId, videoId, interactionType唯一表示一条记录
        long userId = userInteraction.getUserId();
        long videoId = userInteraction.getVideoId();
        EnumInteractionType interactionType = userInteraction.getInteractionType();

        LambdaQueryWrapper<UserInteraction> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(UserInteraction::getUserId, userId)
                .eq(UserInteraction::getVideoId, videoId)
                .eq(UserInteraction::getInteractionType, interactionType);
        int deleteResult = userInteractionMapper.delete(lambdaQueryWrapper);
        System.out.println(deleteResult);
    }
}
