package com.example.webswipeservice.service.video.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.webswipeservice.mapper.user.UserInfoMapper;
import com.example.webswipeservice.mapper.video.CategoryInfoMapper;
import com.example.webswipeservice.mapper.video.VideoInfoMapper;
import com.example.webswipeservice.modal.user.UserInfo;
import com.example.webswipeservice.modal.video.CategoryInfo;
import com.example.webswipeservice.modal.video.UploadedVideo;
import com.example.webswipeservice.modal.video.VideoInfo;
import com.example.webswipeservice.service.video.VideoInfoService;
import com.example.webswipeservice.tools.VideoTool;
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
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VideoInfoServiceImpl implements VideoInfoService {

    @Autowired
    VideoInfoMapper videoMapper;
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    CategoryInfoMapper categoryInfoMapper;

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
        return VideoTool.buildQlySrcUrl(videoDomain, false, key, expireInSeconds, accessKey, secretKey);
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
            String videoUrl = VideoTool.buildQlySrcUrl(videoDomain, false, videoInfo.getVideoKey(), expireInSeconds, accessKey, secretKey);
            String coverUrl = VideoTool.buildQlySrcUrl(coverDomain, false, videoInfo.getCoverKey(), expireInSeconds, accessKey, secretKey);
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

    public void uploadVideo(UploadedVideo uploadedVideo) throws QiniuException {
        Date createAt = new Date();

        uploadedVideo.setCoverOffset(1);

        // 1. 保存视频数据到七牛云
        Response response = VideoTool.uploadVideo2Qly(uploadedVideo.getFile(), videoBucket, accessKey, secretKey);
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        String videoKey = putRet.key;

        // 2. 调用七牛云接口，生成封面并上传到web-swipe-cover-video空间
        // 根据videoKey生成coverKey
        String coverKey = videoKey;
        VideoTool.uploadCover2Qly(uploadedVideo, videoBucket, videoKey, accessKey, secretKey, coverBucket, coverKey, coverPipeline);

        // 3. 生成VideoInfo对象
        long uploaderId = 1;
        double duration = 1;
        String categories = uploadedVideo.getCategories();

        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setVideoKey(videoKey);
        videoInfo.setCoverKey(coverKey);
        videoInfo.setUploaderId(uploaderId);
        videoInfo.setCreateAt(createAt);
        videoInfo.setDuration(duration);
        videoInfo.setCategories(categories);
        videoInfo.setDescription(uploadedVideo.getDescription());

        // 4. 保存到数据库中
        videoMapper.insert(videoInfo);
    }
}
