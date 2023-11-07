package com.example.webswipeservice.aspect;

import com.example.webswipeservice.mapper.user.UserInfoMapper;
import com.example.webswipeservice.mapper.userinteraction.UserInteractionMapper;
import com.example.webswipeservice.mapper.video.CategoryInfoMapper;
import com.example.webswipeservice.mapper.video.VideoInfoMapper;
import com.example.webswipeservice.modal.user.UserInfo;
import com.example.webswipeservice.modal.user.controller.UserInfoHolder;
import com.example.webswipeservice.modal.video.VideoInfo;
import com.example.webswipeservice.tools.QlyTool;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class VideoServiceAspect {

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

    @Around("execution(* com.example.webswipeservice.service.video.VideoInfoService.list(..))" +
            "|| execution(* com.example.webswipeservice.service.video.VideoInfoService.search(..))" +
            "|| execution(* com.example.webswipeservice.service.video.VideoInfoService.selectInteractionVideo(..)) " +
            "|| execution(* com.example.webswipeservice.service.video.VideoInfoService.selectByUserId(..))")
    public List<VideoInfo> aroundVideoServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        // 调用原始方法，这将触发原始方法的执行
        List<VideoInfo> videoInfos = (List<VideoInfo>) joinPoint.proceed();

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
}
