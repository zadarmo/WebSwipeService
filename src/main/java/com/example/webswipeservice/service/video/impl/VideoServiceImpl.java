package com.example.webswipeservice.service.video.impl;

import com.example.webswipeservice.service.video.VideoService;
import com.example.webswipeservice.tools.VideoTool;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {

    @Value("${qly-user.access-key}")
    String accessKey;
    @Value("${qly-user.secret-key}")
    String secretKey;

    @Value("${qly-buckets.web-swipe.domain}")
    String domain;
    @Value("${qly-buckets.web-swipe.bucket}")
    String bucket;

    @Override
    public String download(String key) throws QiniuException {
        long expireInSeconds = 3600;
        return VideoTool.downloadVideoFromQly(domain, false, key, expireInSeconds, accessKey, secretKey);
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
