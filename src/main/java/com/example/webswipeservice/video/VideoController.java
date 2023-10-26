/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.webswipeservice.video;

import com.example.webswipeservice.demos.web.User;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.DownloadUrl;
import com.qiniu.storage.Region;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/video")
public class VideoController {
    @Value("${qiniuyun.domain}")
    private String domain;
    @Value("${qiniuyun.bucket}")
    private String bucket;
    @Value("${qiniuyun.access-key}")
    private String accessKey;
    @Value("${qiniuyun.secret-key}")
    private String secretKey;

    /**
     * 下载一个视频
     * @return 返回请求的视频经过封装后的可直接访问的链接
     * @throws QiniuException
     */
    @RequestMapping("/download")
    @ResponseBody
    public String download(@RequestParam String key) throws QiniuException {
        // domain   下载 domain, eg: qiniu.com【必须】
        // useHttps 是否使用 https【必须】
        // key      下载资源在七牛云存储的 key【必须】
        boolean useHttps = false;
        DownloadUrl url = new DownloadUrl(domain, useHttps, key);
//        url.setAttname(attname) // 配置 attname
//                .setFop(fop) // 配置 fop
//                .setStyle(style, styleSeparator, styleParam) // 配置 style

        // 带有效期
        long expireInSeconds = 3600;//1小时，可以自定义链接过期时间
        long deadline = System.currentTimeMillis()/1000 + expireInSeconds;
        Auth auth = Auth.create(accessKey, secretKey);
        return url.buildURL(auth, deadline);
    }

    /**
     * 列举空间文件列表
     * @return 字符串列表，存放所有文件的key值
     */
    @RequestMapping("/listall")
    @ResponseBody
    public List<String> listall() {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region2()); // 华南机房对应的Region
        //...其他参数参考类注释

        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);

        //文件名前缀
        String prefix = "";
        //每次迭代的长度限制，最大1000，推荐值 1000
        int limit = 1000;
        //指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
        String delimiter = "";

        //列举空间文件列表
        List<String> keyList = new ArrayList<>();
        BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(bucket, prefix, limit, delimiter);
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
