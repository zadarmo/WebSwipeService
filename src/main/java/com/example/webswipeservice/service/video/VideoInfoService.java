package com.example.webswipeservice.service.video;

import com.example.webswipeservice.modal.video.CategoryInfo;
import com.example.webswipeservice.modal.video.UploadedVideo;
import com.example.webswipeservice.modal.video.VideoInfo;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;

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
    List<VideoInfo> list(String category) throws QiniuException;

    // 查询所有视频类别
    List<CategoryInfo> listCategories();

    // 上传一个视频
    int uploadVideo(UploadedVideo uploadedVideo) throws QiniuException;

    // 根据用户id查询视频
    List<VideoInfo> selectByUserId(long id);

    // 获取用户互动的视频
    List<VideoInfo> selectInteractionVideo(long userId, String interactionType);

    // 搜索视频
    List<VideoInfo> search(String desc, boolean latest) throws QiniuException;
}
