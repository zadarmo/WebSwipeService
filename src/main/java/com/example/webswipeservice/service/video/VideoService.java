package com.example.webswipeservice.service.video;

import com.example.webswipeservice.modal.video.VideoInfo;
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

    // 查询单个标签的视频列表
    List<VideoInfo> list(String tag) throws QiniuException;
}
