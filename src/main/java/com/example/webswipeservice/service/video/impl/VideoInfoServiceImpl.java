package com.example.webswipeservice.service.video.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.webswipeservice.mapper.user.UserInfoMapper;
import com.example.webswipeservice.mapper.userinteraction.UserInteractionMapper;
import com.example.webswipeservice.mapper.video.CategoryInfoMapper;
import com.example.webswipeservice.mapper.video.VideoInfoMapper;
import com.example.webswipeservice.modal.user.UserInfo;
import com.example.webswipeservice.modal.user.controller.UserInfoHolder;
import com.example.webswipeservice.modal.userinteraction.UserInteraction;
import com.example.webswipeservice.modal.video.CategoryInfo;
import com.example.webswipeservice.modal.video.UploadedVideo;
import com.example.webswipeservice.modal.video.VideoInfo;
import com.example.webswipeservice.service.userinteraction.UserInteractionService;
import com.example.webswipeservice.service.video.VideoInfoService;
import com.example.webswipeservice.tools.QlyTool;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import com.qiniu.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VideoInfoServiceImpl implements VideoInfoService {

    @Autowired
    VideoInfoMapper videoMapper;
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    CategoryInfoMapper categoryInfoMapper;
    @Autowired
    UserInteractionMapper userInteractionMapper;

    @Value("${qly-user.access-key}")
    String accessKey;
    @Value("${qly-user.secret-key}")
    String secretKey;

    @Value("${qly-buckets.web-swipe.domain}")
    String videoDomain;
    @Value("${qly-buckets.web-swipe.bucket}")
    String videoBucket;
    @Value("${qly-buckets.web-swipe-video-cover.domain}")
    String coverDomain;
    @Value("${qly-buckets.web-swipe-video-cover.bucket}")
    String coverBucket;

    @Value("${qly-pipelines.cover}")
    String coverPipeline;

    @Value("${qly-expireInSeconds}")
    long expireInSeconds;

    public List<VideoInfo> list(String category) throws QiniuException {
        if (StringUtils.isNullOrEmpty(category) || category.equals("popular")) {
            return videoMapper.selectList(null);
        } else {
            // 从数据库中查询满足tags字段包含tag的数据
            QueryWrapper<VideoInfo> queryMapper = new QueryWrapper<>();
            queryMapper.like("categories", category);
            return videoMapper.selectList(queryMapper);
        }
    }

    @Override
    public List<CategoryInfo> listCategories() {
        return categoryInfoMapper.selectList(null);
    }

    public int uploadVideo(UploadedVideo uploadedVideo) throws QiniuException {
        // 获取上传用户的id
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        long userId = userInfo.getId();

        Date createAt = new Date();
        // 随机生成封面高度，并返回
        int coverH = -1;
        if (uploadedVideo.getIsVertical() == 1) {
            coverH = 450;
        } else {
            coverH = 350;
        }
        uploadedVideo.setCoverH(coverH);

        // 1. 保存视频数据到七牛云
        Response response = QlyTool.uploadSrc2Qly(uploadedVideo.getFile(), videoBucket, accessKey, secretKey);
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        String videoKey = putRet.key;

        // 2. 调用七牛云接口，生成封面并上传到web-swipe-cover-video空间
        // 根据videoKey生成coverKey
//        String coverKey = videoKey;
        String coverKey = String.valueOf(new Date().getTime());
        QlyTool.uploadCover2Qly(uploadedVideo, videoBucket, videoKey, accessKey, secretKey, coverBucket, coverKey, coverPipeline);

        // 3. 生成VideoInfo对象
        double duration = 1;
        String categories = uploadedVideo.getCategories();

        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setVideoKey(videoKey);
        videoInfo.setCoverKey(coverKey);

        videoInfo.setUploaderId(userInfo.getId());
        videoInfo.setCreateAt(createAt);
        videoInfo.setUploaderId(userId);
        videoInfo.setDuration(duration);
        videoInfo.setCategories(categories);
        videoInfo.setDescription(uploadedVideo.getDescription());

        // 4. 保存到数据库中
        videoMapper.insert(videoInfo);

        // 5. 返回封面高度
        return coverH;
    }

    @Override
    public List<VideoInfo> selectByUserId() {
        UserInfo userInfo = UserInfoHolder.getUserInfo();
        if (Objects.isNull(userInfo)) {
            return null;
        } else {
            LambdaQueryWrapper<VideoInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(VideoInfo::getUploaderId, userInfo.getId());
            return videoMapper.selectList(lambdaQueryWrapper);
        }
    }

    @Override
    public List<VideoInfo> selectInteractionVideo(String interactionType) {
        UserInfo userInfo = UserInfoHolder.getUserInfo();
        if (Objects.isNull(userInfo)) {
            return null;
        } else {
            // 1. 在user_interaction表中获取用户id为userId互动为InteractionType的视频id
            LambdaQueryWrapper<UserInteraction> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper
                    .eq(UserInteraction::getUserId, userInfo.getId())
                    .eq(UserInteraction::getInteractionType, interactionType);
            List<UserInteraction> userInteractions = userInteractionMapper.selectList(lambdaQueryWrapper);

            // 2. 根据视频id，查询视频的所有信息
            if (!userInteractions.isEmpty()) {
                // Extract video_id values from the result
                List<Long> videoIds = userInteractions.stream()
                        .map(UserInteraction::getVideoId)
                        .collect(Collectors.toList());

                // Query table2 for video_info using the video_id values
                LambdaQueryWrapper<VideoInfo> lambdaQueryWrapper2 = new LambdaQueryWrapper<>();
                lambdaQueryWrapper2.in(VideoInfo::getId, videoIds);
                List<VideoInfo> videoInfos = videoMapper.selectList(lambdaQueryWrapper2);
                return videoInfos;
            }

            // Handle the case when there are no records in table1
            return Collections.emptyList();
        }
    }

    @Override
    public List<VideoInfo> search(String desc, boolean latest) throws QiniuException {
        // 模糊查询描述
        LambdaQueryWrapper<VideoInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(VideoInfo::getDescription, desc);
        if (latest) {
            lambdaQueryWrapper.orderByDesc(VideoInfo::getCreateAt);
        }
        return videoMapper.selectList(lambdaQueryWrapper);
    }
}
