package com.example.webswipeservice.service.video;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Region;

import java.util.List;

/**
 * 七牛云视频service
 */
public interface VideoService {
    // 下载一个视频
    String download(String domain, boolean useHttps, String key, long expireInSeconds, String accessKey, String secretKey) throws QiniuException;

    // 列举空间文件列表
    List<String> listAll(Region region, String accessKey, String secretKey, String bucket, String prefix, int limit, String delimiter);
}
