package com.example.webswipeservice.mapper.video;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.webswipeservice.modal.user.UserInfo;
import com.example.webswipeservice.modal.video.VideoInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VideoMapper extends BaseMapper<VideoInfo> {
}
