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

package com.example.webswipeservice.controller.userinteraction;

import com.example.webswipeservice.modal.userinteraction.UserInteraction;
import com.example.webswipeservice.network.BaseResponse;
import com.example.webswipeservice.network.ResultUtils;
import com.example.webswipeservice.service.userinteraction.UserInteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/userinteraction")
public class UserInteractionController {
    @Autowired
    UserInteractionService userInteractionService;

    /**
     * 用户对视频执行点赞、收藏等动作
     * @return
     */
    @PostMapping("/act")
    public BaseResponse<String> act(@RequestBody UserInteraction userInteraction) {
        userInteractionService.add(userInteraction);
        return ResultUtils.success("success", null);
    }

    /**
     * 用户对视频取消点赞、收藏等动作
     * @return
     */
    @PostMapping("/cancel")
    public BaseResponse<String> cancel(@RequestBody UserInteraction userInteraction) {
        userInteractionService.delete(userInteraction);
        return ResultUtils.success("success", null);
    }
}
