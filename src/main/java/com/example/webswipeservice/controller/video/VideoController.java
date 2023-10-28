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

package com.example.webswipeservice.controller.video;

import com.example.webswipeservice.constant.BucketsWebSwipeConstant;
import com.example.webswipeservice.constant.UserConstant;
import com.example.webswipeservice.service.video.VideoService;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/video")
public class VideoController {
    @Autowired
    UserConstant userConstant;
    @Autowired
    BucketsWebSwipeConstant bucketsWebSwipeConstant;
    @Autowired
    VideoService videoService;

    /**
     * 下载一个视频
     * @return 返回请求的视频经过封装后的可直接访问的链接
     * @throws QiniuException
     */
    @RequestMapping("/download")
    public String download(@RequestParam String key) throws QiniuException {
        return videoService.download(bucketsWebSwipeConstant, userConstant, false, key, 3600);
    }

    /**
     * 列举空间文件列表
     * @return 字符串列表，存放所有文件的key值
     */
    @RequestMapping("/listall")
    public List<String> listAll() {
        return videoService.listAll(bucketsWebSwipeConstant, userConstant, Region.region2(), "", 1000, "");
    }
}
