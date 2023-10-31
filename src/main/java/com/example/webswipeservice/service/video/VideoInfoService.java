package com.example.webswipeservice.service.video;

import com.example.webswipeservice.modal.video.CategoryInfo;
import com.example.webswipeservice.modal.video.VideoInfo;
import com.qiniu.common.QiniuException;

import java.util.List;

/**
 * 七牛云视频service
 */
public interface VideoInfoService {
    // 下载一个视频
    String download(String key) throws QiniuException;

    // 列举空间文件列表
    List<String> listAll();

    // 查询单个标签的视频列表
    List<VideoInfo> list(String tag) throws QiniuException;

    // 查询所有视频标签
    List<CategoryInfo> listTags();
}
