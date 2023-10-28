package com.example.webswipeservice.service.video.impl;

import com.example.webswipeservice.constant.BucketsWebSwipeConstant;
import com.example.webswipeservice.constant.UserConstant;
import com.example.webswipeservice.service.video.VideoService;
import com.example.webswipeservice.tools.VideoTool;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {
    @Override
    public String download(BucketsWebSwipeConstant bucketsWebSwipeConstant, UserConstant userConstant, boolean useHttps, String key, long expireInSeconds) throws QiniuException {
        return VideoTool.downloadVideoFromQly(bucketsWebSwipeConstant.getDomain(), useHttps, key, expireInSeconds, userConstant.getAccessKey(), userConstant.getSecretKey());
    }

    @Override
    public List<String> listAll(BucketsWebSwipeConstant bucketsWebSwipeConstant, UserConstant userConstant, Region region, String prefix, int limit, String delimiter) {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(region); // 华南机房对应的Region
        //...其他参数参考类注释

        Auth auth = Auth.create(userConstant.getAccessKey(), userConstant.getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, cfg);

        //列举空间文件列表
        BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(bucketsWebSwipeConstant.getBucket(), prefix, limit, delimiter);
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
}
