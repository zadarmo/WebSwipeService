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
import com.qiniu.storage.DownloadUrl;
import com.qiniu.util.Auth;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/video")
public class VideoController {

    // http://127.0.0.1:8080/hello?name=lisi
    @RequestMapping("/download")
    @ResponseBody
    public String download() throws QiniuException {
        // domain   下载 domain, eg: qiniu.com【必须】
        // useHttps 是否使用 https【必须】
        // key      下载资源在七牛云存储的 key【必须】
        String domain = "s35106op1.hn-bkt.clouddn.com";
        boolean useHttps = false;
        String key = "1C319BD690097A1D186BAC68FB743B93.mp4";
        DownloadUrl url = new DownloadUrl(domain, useHttps, key);
//        url.setAttname(attname) // 配置 attname
//                .setFop(fop) // 配置 fop
//                .setStyle(style, styleSeparator, styleParam) // 配置 style

        // 带有效期
        long expireInSeconds = 3600;//1小时，可以自定义链接过期时间
        long deadline = System.currentTimeMillis()/1000 + expireInSeconds;
        String accessKey = "Zmp5QecX_NwwnBFKOAfOjmFVb15CIR2x4-jedmuQ";
        String secretKey = "HnuNUH9lumfXoFxXcrrelbucVkTXG5Hg00VxL9Sn";
        Auth auth = Auth.create(accessKey, secretKey);
        String urlString = url.buildURL(auth, deadline);
        System.out.println(urlString);
        return urlString;
    }
}
