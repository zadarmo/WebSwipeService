package com.example.webswipeservice.service.video.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.webswipeservice.mapper.video.VideoMapper;
import com.example.webswipeservice.modal.video.VideoInfo;
import com.example.webswipeservice.service.video.VideoService;
import com.example.webswipeservice.tools.VideoTool;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    VideoMapper videoMapper;

    @Value("${qly-user.access-key}")
    String accessKey;
    @Value("${qly-user.secret-key}")
    String secretKey;

    @Value("${qly-buckets.web-swipe.domain}")
    String webSwipeDomain;
    @Value("${qly-buckets.web-swipe.bucket}")
    String webSwipeBucket;
    @Value("${qly-buckets.web-swipe-video-cover.domain}")
    String webSwipeVideoCoverDomain;
    @Value("${qly-buckets.web-swipe-video-cover.bucket}")
    String webSwipeVideoCoverBucket;

    @Override
    public String download(String key) throws QiniuException {
        long expireInSeconds = 3600;
        return VideoTool.buildQlySrcUrl(webSwipeDomain, false, key, expireInSeconds, accessKey, secretKey);
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
        BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(webSwipeBucket, prefix, limit, delimiter);
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

    public List<VideoInfo> list(String tag) throws QiniuException {
        long expireInSeconds = 3600;

        // 从数据库中查询满足tags字段包含tag的数据
        QueryWrapper<VideoInfo> queryMapper = new QueryWrapper<>();
        queryMapper.like("tags", tag);
        List<VideoInfo> videoInfos = videoMapper.selectList(queryMapper);

        // 构建资源外链
        for (VideoInfo videoInfo : videoInfos) {
            String videoUrl = VideoTool.buildQlySrcUrl(webSwipeDomain, false, videoInfo.getVideoKey(), expireInSeconds, accessKey, secretKey);
            String coverUrl = VideoTool.buildQlySrcUrl(webSwipeVideoCoverDomain, false, videoInfo.getCoverKey(), expireInSeconds, accessKey, secretKey);
            videoInfo.setVideoUrl(videoUrl);
            videoInfo.setCoverUrl(coverUrl);
        }
        return videoInfos;
    }

    @Override
    public List<String> listTags() {
        // 查询所有视频数据
        Set<String> tagSet = new HashSet<>();
        List<VideoInfo> videoInfos = videoMapper.selectList(null);

        // 分割tags字段，存入set
        for(VideoInfo videoInfo : videoInfos) {
            String tags = videoInfo.getTags();
            String[] tagArr = tags.split(",");
            tagSet.addAll(Arrays.asList(tagArr));
        }

        // 添加“热门”分类
        List<String> tagList = new ArrayList<>();
        tagList.add("热门");
        tagList.addAll(tagSet);
        return tagList;
    }
}
