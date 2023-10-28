package com.example.webswipeservice.service.user.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.webswipeservice.mapper.user.UserInfoMapper;
import com.example.webswipeservice.modal.user.UserInfo;
import com.example.webswipeservice.service.user.UserInfoService;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
}
