package com.example.webswipeservice.tools;

import com.example.webswipeservice.modal.video.UploadedVideo;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.processing.OperationManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.DownloadUrl;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

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

    /**
     * 上传视频到七牛云
     * @param file
     * @param bucket
     * @param accessKey
     * @param secretKey
     * @return
     */
    public static Response uploadVideo2Qly(MultipartFile file, String bucket, String accessKey, String secretKey) {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region2());
//        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本

        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = null;
        try {
            byte[] uploadBytes = file.getBytes();
            ByteArrayInputStream byteInputStream=new ByteArrayInputStream(uploadBytes);
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            return uploadManager.put(byteInputStream, key, upToken,null, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据视频生成封面，并上传视频封面到七牛云
     * @param uploadedVideo
     * @param videoBucket
     * @param videoKey
     * @param accessKey
     * @param secretKey
     * @param coverBucket
     * @param coverKey
     * @param pipeline
     */
    public static void uploadCover2Qly(
            UploadedVideo uploadedVideo,
            String videoBucket,
            String videoKey,
            String accessKey,
            String secretKey,

            String coverBucket,
            String coverKey,

            String pipeline
    ) {
        //设置账号的AK,SK
        Auth auth = Auth.create(accessKey, secretKey);
        //新建一个OperationManager对象
        Configuration cfg = new Configuration(Region.region2());
        OperationManager operater = new OperationManager(auth, cfg);
        //设置转码操作参数
        String coverImgType = uploadedVideo.getCoverImgType();
        int coverOffset = uploadedVideo.getCoverOffset();
        int coverH = uploadedVideo.getCoverH();
        int coverW = uploadedVideo.getCoverW();
        String coverRotate = "auto";

        // 拼接vframe接口字符串
        StringBuilder sb = new StringBuilder();
        sb.append("vframe");
        sb.append("/").append(coverImgType)
                .append("/offset/").append(coverOffset)
                .append("/w/").append(coverW)
                .append("/h/").append(coverH)
                .append("/rotate/").append(coverRotate);
        String fops = sb.toString();

        //可以对转码后的文件进行使用saveas参数自定义命名，当然也可以不指定文件会默认命名并保存在当前空间。
        String urlbase64 = UrlSafeBase64.encodeToString(coverBucket + ":" + coverKey);
        String pfops = fops + "|saveas/" + urlbase64;
        //设置pipeline参数
        StringMap params = new StringMap().putWhen("force", 1, true).putNotEmpty("pipeline", pipeline);
        try {
            String persistid = operater.pfop(videoBucket, videoKey, pfops, params);
            //打印返回的persistid, 可用于查询持久化状态
            System.out.println(persistid);
        } catch (QiniuException e) {
            System.out.println(e.getMessage());
        }
    }
}
