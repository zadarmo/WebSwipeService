package com.example.webswipeservice.mapper.userinteraction;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.webswipeservice.modal.user.UserInfo;
import com.example.webswipeservice.modal.userinteraction.UserInteraction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInteractionMapper extends BaseMapper<UserInteraction> {
}
