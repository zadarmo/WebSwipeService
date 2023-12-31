package com.example.webswipeservice.modal.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.webswipeservice.constant.EnumSex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("user_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    @TableId
    private long id;
    private String username;
    private String password;
    private String avatarKey;

    @TableField(exist = false)
    private String avatarUrl;
    public void setAvatarUrl(String url) {
        this.avatarUrl = url;
    }
}
