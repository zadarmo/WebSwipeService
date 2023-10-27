package com.example.webswipeservice.service.video.impl;

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
    public String download(String domain, boolean useHttps, String key, long expireInSeconds, String accessKey, String secretKey) throws QiniuException {
        return VideoTool.downloadVideoFromQly(domain, useHttps, key, expireInSeconds, accessKey, secretKey);
    }

    @Override
    public List<String> listAll(Region region, String accessKey, String secretKey, String bucket, String prefix, int limit, String delimiter) {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(region); // 华南机房对应的Region
        //...其他参数参考类注释

        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);

        //列举空间文件列表
        BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(bucket, prefix, limit, delimiter);
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
