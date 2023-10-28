package com.example.webswipeservice.service.video;

import com.example.webswipeservice.constant.BucketsWebSwipeConstant;
import com.example.webswipeservice.constant.UserConstant;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.Region;

import java.util.List;

/**
 * 七牛云视频service
 */
public interface VideoService {
    // 下载一个视频
    String download(BucketsWebSwipeConstant bucketsWebSwipeConstant, UserConstant userConstant, boolean useHttps, String key, long expireInSeconds) throws QiniuException;

    // 列举空间文件列表
    List<String> listAll(BucketsWebSwipeConstant bucketsWebSwipeConstant, UserConstant userConstant, Region region, String prefix, int limit, String delimiter);
}
