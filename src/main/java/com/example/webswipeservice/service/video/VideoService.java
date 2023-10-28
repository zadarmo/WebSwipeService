package com.example.webswipeservice.service.video;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.Region;

import java.util.List;

/**
 * 七牛云视频service
 */
public interface VideoService {
    // 下载一个视频
    String download(String key) throws QiniuException;

    // 列举空间文件列表
    List<String> listAll();
}
