package com.example.webswipeservice.service.video.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.webswipeservice.mapper.user.UserInfoMapper;
import com.example.webswipeservice.mapper.userinteraction.UserInteractionMapper;
import com.example.webswipeservice.mapper.video.CategoryInfoMapper;
import com.example.webswipeservice.mapper.video.VideoInfoMapper;
import com.example.webswipeservice.modal.user.UserInfo;
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

    @Override
    public String download(String key) throws QiniuException {
        long expireInSeconds = 3600;
        return QlyTool.buildQlySrcUrl(videoDomain, false, key, expireInSeconds, accessKey, secretKey);
    }

    @Override
    public List<String> listAll() {
        String prefix = "";
        int limit = 1000;
        String delimiter = "";

        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region2()); // 华南机房对应的Region
        //...其他参数参考类注释

        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);

        //列举空间文件列表
        BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(videoBucket, prefix, limit, delimiter);
        List<String> keyList = new ArrayList<>();
        while (fileListIterator.hasNext()) {
            //处理获取的file list结果
            FileInfo[] items = fileListIterator.next();
            for (FileInfo item : items) {
                keyList.add(item.key);
            }
        }
        return keyList;
    }

    public List<VideoInfo> list(String category) throws QiniuException {
        long expireInSeconds = 3600;

        // 从数据库中查询满足tags字段包含tag的数据
        QueryWrapper<VideoInfo> queryMapper = new QueryWrapper<>();
        queryMapper.like("categories", category);
        List<VideoInfo> videoInfos = videoMapper.selectList(queryMapper);

        // 构建资源外链
        for (VideoInfo videoInfo : videoInfos) {
            String videoUrl = QlyTool.buildQlySrcUrl(videoDomain, false, videoInfo.getVideoKey(), expireInSeconds, accessKey, secretKey);
            String coverUrl = QlyTool.buildQlySrcUrl(coverDomain, false, videoInfo.getCoverKey(), expireInSeconds, accessKey, secretKey);
            videoInfo.setVideoUrl(videoUrl);
            videoInfo.setCoverUrl(coverUrl);

            // 查询用户名
            UserInfo userInfo = userInfoMapper.selectById(videoInfo.getUploaderId());
            videoInfo.setUsername(userInfo.getUsername());
        }

        return videoInfos;
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
    public List<VideoInfo> selectByUserId(long id) {
        LambdaQueryWrapper<VideoInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(VideoInfo::getUploaderId, id);
        return videoMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<VideoInfo> selectInteractionVideo(long userId, String interactionType) {
        // 1. 在user_interaction表中获取用户id为userId互动为InteractionType的视频id
        LambdaQueryWrapper<UserInteraction> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(UserInteraction::getUserId, userId)
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
