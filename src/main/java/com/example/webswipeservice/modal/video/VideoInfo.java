package com.example.webswipeservice.modal.video;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@TableName("video_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoInfo {
    @TableId
    private long id;
    private String videoKey;
    private String coverKey;
    private long uploaderId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createAt;
    private double duration;
    private String categories;
    private String description;

    @TableField(exist = false)
    private String videoUrl;
    @TableField(exist = false)
    private String coverUrl;
    @TableField(exist = false)
    private String username;
    public void setVideoUrl(String url) {
        this.videoUrl = url;
    }
    public void setCoverUrl(String url) {
        this.coverUrl = url;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
