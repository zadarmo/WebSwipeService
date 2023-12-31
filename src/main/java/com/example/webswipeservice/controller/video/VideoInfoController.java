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

import com.example.webswipeservice.modal.user.UserInfo;
import com.example.webswipeservice.modal.video.CategoryInfo;
import com.example.webswipeservice.modal.video.UploadedVideo;
import com.example.webswipeservice.modal.video.VideoInfo;
import com.example.webswipeservice.network.BaseResponse;
import com.example.webswipeservice.network.ResultUtils;
import com.example.webswipeservice.service.video.VideoInfoService;
import com.example.webswipeservice.tools.QlyTool;
import com.qiniu.common.QiniuException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/video")
public class VideoInfoController {
    @Autowired
    VideoInfoService videoService;

    /**
     * 查询单个标签的视频列表
     * @return VideoInfo列表
     */
    @RequestMapping("/list")
    public BaseResponse<Object> list(@RequestParam(required = false) String category) throws QiniuException {
        if (category == null) {
            // TODO
            return ResultUtils.success("success", videoService.list("popular"));
        } else {
            return ResultUtils.success("success", videoService.list(category));
        }
    }

    /**
     * 查询所有视频类别
     * @return CategoryInfo列表
     */
    @RequestMapping("/listcategories")
    public BaseResponse<List<CategoryInfo>> listCategories()  {
        return ResultUtils.success("success", videoService.listCategories());
    }

    /**
     * 上传一个视频
     * @return 随机生成的封面高度
     */
    @PostMapping("/upload")
    public BaseResponse<Map<String, Integer>> upload(@ModelAttribute UploadedVideo uploadedVideo) throws QiniuException {
        int coverH = videoService.uploadVideo(uploadedVideo);
        Map<String, Integer> map = new HashMap<>();
        map.put("coverH", coverH);
        return ResultUtils.success("success", map);
    }

    /**
     * 根据用户id查询视频
     * @return
     */
    @PostMapping("/getuploadvideo")
    public BaseResponse<Object> getUploadVideo() {
        List<VideoInfo> videoInfos = videoService.selectByUserId();
        if (Objects.isNull(videoInfos)) {
            return ResultUtils.error(-1, "当前未登录");
        } else {
            return ResultUtils.success("success", videoInfos);
        }
    }

    /**
     * 获取用户互动的视频
     * @return
     */
    @GetMapping("/getinteractionvideo")
    public BaseResponse<Object> getinteractionvideo(@RequestParam(value="interactionType") String interactionType) {
        List<VideoInfo> videoInfos = videoService.selectInteractionVideo(interactionType);
        if (Objects.isNull(videoInfos)) {
            return ResultUtils.error(-1,"当前未登录");
        } else {
            return ResultUtils.success("success", videoInfos);
        }
    }

    /**
     * 搜索视频
     * @return
     */
    @GetMapping("/search")
    public BaseResponse<List<VideoInfo>> search(@RequestParam String desc, @RequestParam(required = false) Boolean latest) throws QiniuException {
        if (latest == null) {
            latest = false;
        }
        return ResultUtils.success("success", videoService.search(desc, latest));
    }
}
