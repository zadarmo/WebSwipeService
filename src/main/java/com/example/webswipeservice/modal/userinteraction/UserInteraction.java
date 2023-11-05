package com.example.webswipeservice.modal.userinteraction;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.webswipeservice.constant.EnumInteractionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@TableName("user_interaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInteraction {
    @TableId
    private long id;
    private long userId;
    private long videoId;
    private EnumInteractionType interactionType;
    private Date operateAt;
}
