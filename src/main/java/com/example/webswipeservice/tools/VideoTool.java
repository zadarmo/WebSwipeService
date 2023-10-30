package com.example.webswipeservice.tools;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.DownloadUrl;
import com.qiniu.util.Auth;

public class VideoTool {
    /**
     * 生成七牛云的资源访问外链
     * @param domain 域名
     * @param useHttps 是否使用https协议
     * @param key 视频key
     * @param expireInSeconds 有效期, 单位为秒
     * @param accessKey 公钥
     * @param secretKey 私钥
     * @return 资源外链
     */

    public static String buildQlySrcUrl(String domain, boolean useHttps, String key, long expireInSeconds, String accessKey, String secretKey) throws QiniuException {
        // domain   下载 domain, eg: qiniu.com【必须】
        // useHttps 是否使用 https【必须】
        // key      下载资源在七牛云存储的 key【必须】
        DownloadUrl url = new DownloadUrl(domain, useHttps, key);
        //        url.setAttname(attname) // 配置 attname
        //                .setFop(fop) // 配置 fop
        //                .setStyle(style, styleSeparator, styleParam) // 配置 style

        // 带有效期
        long deadline = System.currentTimeMillis()/1000 + expireInSeconds;
        Auth auth = Auth.create(accessKey, secretKey);
        return url.buildURL(auth, deadline);
    }
}
